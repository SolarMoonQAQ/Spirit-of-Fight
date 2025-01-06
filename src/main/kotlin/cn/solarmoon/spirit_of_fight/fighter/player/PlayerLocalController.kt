package cn.solarmoon.spirit_of_fight.fighter.player

import cn.solarmoon.spark_core.animation.vanilla.asAnimatable
import cn.solarmoon.spark_core.entity.preinput.PreInput
import cn.solarmoon.spark_core.entity.preinput.getPreInput
import cn.solarmoon.spark_core.entity.state.getInputVector
import cn.solarmoon.spark_core.entity.state.isJumping
import cn.solarmoon.spark_core.local_control.LocalInputController
import cn.solarmoon.spark_core.skill.getTypedSkillController
import cn.solarmoon.spark_core.util.MoveDirection
import cn.solarmoon.spirit_of_fight.data.SOFSkillTags
import cn.solarmoon.spirit_of_fight.feature.fight_skill.controller.FightSkillController
import cn.solarmoon.spirit_of_fight.feature.fight_skill.controller.SwordFightSkillController
import cn.solarmoon.spirit_of_fight.feature.fight_skill.sync.ClientOperationPayload
import cn.solarmoon.spirit_of_fight.registry.client.SOFKeyMappings
import net.minecraft.client.Minecraft
import net.minecraft.client.player.Input
import net.minecraft.client.player.LocalPlayer
import net.minecraft.world.phys.HitResult
import net.minecraft.world.phys.Vec3
import net.neoforged.neoforge.client.event.InputEvent
import net.neoforged.neoforge.client.event.MovementInputUpdateEvent

object PlayerLocalController: LocalInputController() {

    var jumpContainTick = 0
    var guardContainTick = 0
    val attackKey get() = Minecraft.getInstance().options.keyAttack

    override fun laterInit() {
        addTickingKey(attackKey)
        addTickingKey(SOFKeyMappings.GUARD)
        addTickingKey(SOFKeyMappings.PARRY)
    }

    fun attack(player: LocalPlayer, skillController: FightSkillController) {
        val preInput = player.getPreInput()
        when {
            jumpContainTick < 0 -> {
                jumpContainTick = 0
                preInput.setInput("jump_attack", 5) {
                    skillController.getJumpAttackSkill().activate()
                    addPackage(ClientOperationPayload(player.id, preInput.id, Vec3.ZERO, 0))
                }
            }
            player.isSprinting -> {
                preInput.setInput("sprinting_attack", 5) {
                    skillController.getSprintingAttackSkill().activate()
                    addPackage(ClientOperationPayload(player.id, preInput.id, Vec3.ZERO, 0))
                }
            }
            else -> {
                preInput.setInput("combo", 5) {
                    skillController.comboIndex.increment()
                    skillController.getComboSkill().activate()
                    addPackage(ClientOperationPayload(player.id, preInput.id, Vec3.ZERO, skillController.comboIndex.get()))
                }
            }
        }
    }

    fun guard(player: LocalPlayer, skillController: FightSkillController) {
        if (skillController.getGuardSkill().isActive()) return

        val preInput = player.getPreInput()
        preInput.setInput("guard", 5) {
            skillController.getGuardSkill().activate()
            addPackage(ClientOperationPayload(player.id, preInput.id, Vec3.ZERO, 0))
        }
    }

    fun guardStop(player: LocalPlayer, skillController: FightSkillController) {
        if (skillController.getGuardSkill().isStanding) {
            skillController.getGuardSkill().end()
            addPackage(ClientOperationPayload(player.id, "guard_stop", Vec3.ZERO, 0))
        }
    }

    fun dodge(player: LocalPlayer, skillController: FightSkillController, input: Input) {
        val dodge = skillController.getDodgeSkill()
        val preInput = player.getPreInput()
        if (player.onGround()) {
            val v = player.getInputVector()
            val d = MoveDirection.getByInput(input) ?: return
            preInput.setInput("dodge") {
                dodge.moveVector = v
                dodge.direction = d
                dodge.activate()
                addPackage(ClientOperationPayload(player.id, preInput.id, v, d.id))
            }
        }
    }

    fun parry(player: LocalPlayer, skillController: SwordFightSkillController) {
        if (!skillController.getGuardSkill().isBacking && skillController.getGuardSkill().isActive()) {
            skillController.getParrySkill().activate()
            addPackage(ClientOperationPayload(player.id, "parry", Vec3.ZERO, 0))
        }
    }

    override fun tick(player: LocalPlayer, input: Input) {
        if (player.isJumping()) {
            jumpContainTick = -10
        } else jumpContainTick++

        val skillController = player.getTypedSkillController<FightSkillController>() ?: return

        onRelease(attackKey) {
            if (shouldAttack(player)) {
                attack(player, skillController)
            }
        }

        if (!SOFKeyMappings.GUARD.isDown) {
            guardContainTick++
            if (guardContainTick > 0) guardStop(player, skillController)
        }
        else guardContainTick = -3

        onPress(SOFKeyMappings.GUARD) {
            guard(player, skillController)
        }

        onPressOnce(SOFKeyMappings.PARRY) {
            if (skillController is SwordFightSkillController) {
                parry(player, skillController)
            }
        }

        onPressOnce(SOFKeyMappings.DODGE) {
            dodge(player, skillController, input)
        }

        preInputControl(player, player.getPreInput())
    }

    fun preInputControl(player: LocalPlayer, preInput: PreInput) {
        val skillController = player.getTypedSkillController<FightSkillController>() ?: return

        // 不在进行任何技能时可释放预输入
        if (!skillController.isPlaying()) {
            preInput.executeIfPresent()
        }

        // 连招1-2阶段可以变招
        player.asAnimatable().animData.playData.getMixedAnimation(skillController.getComboSkill(0).animName)?.let {
            if (it.isTickIn(0.05, 0.15)) preInput.executeIfPresent("combo")
        }
    }

    override fun onInteract(player: LocalPlayer, event: InputEvent.InteractionKeyMappingTriggered) {
        if (event.isAttack && shouldAttack(player)) {
            event.setSwingHand(false)
            event.isCanceled = true
        }
    }

    override fun updateMovement(player: LocalPlayer, event: MovementInputUpdateEvent) {
        stop(player, event)
    }

    fun stop(player: LocalPlayer, event: MovementInputUpdateEvent) {
        val input = event.input
        val skillController = player.getTypedSkillController<FightSkillController>() ?: return

        fun stop() {
            input.forwardImpulse = 0f
            input.leftImpulse = 0f
            input.up = false
            input.down = false
            input.left = false
            input.right = false
            input.jumping = false
            input.shiftKeyDown = false
            player.sprintTriggerTime = -1
            player.swinging = false
        }

        // 在普通连招过程中可以按住s阻止前移
        if (input.forwardImpulse < 0 && skillController.getComboSkill().isActive() == true) {
            player.deltaMovement = Vec3(0.0, player.deltaMovement.y, 0.0)
        }

        // 格挡时缓慢行走
        if (skillController.getGuardSkill().isStanding) {
            input.forwardImpulse /= 4f
            input.leftImpulse /= 4f
            input.jumping = false
            input.shiftKeyDown = false
            player.sprintTriggerTime = -1
            player.swinging = false
        }

        // 格挡击退禁止移动
        if (skillController.getGuardSkill().isBacking) {
            stop()
        }

        // 播放指定技能时禁止移动
        if (skillController.allActiveSkills.any { it.`is`(SOFSkillTags.INPUT_FREEZE) }) {
            stop()
        }
    }

    fun shouldAttack(player: LocalPlayer): Boolean {
        player.getTypedSkillController<FightSkillController>() ?: return false
        val hit = Minecraft.getInstance().hitResult ?: return false
        // 如果目标是空气或实体，则无论如何默认进行攻击
        return if (hit.type in listOf(HitResult.Type.ENTITY, HitResult.Type.MISS)) true
        // 如果是方块，则看是否按压时间短于0.25秒，超出则正常挖掘
        else getPressTick(attackKey) < 5
    }

//
//    /**
//     * 在释放技能/受击时禁用除了攻击以外的交互
//     */
//    @SubscribeEvent
//    private fun interactStop(event: InputEvent.InteractionKeyMappingTriggered) {
//        val player = Minecraft.getInstance().player ?: return
//        if ((player is IFightSkillHolder && player.skillController?.isPlayingSkill() == true) || (player as IEntityAnimatable<*>).shouldOperateFreezing()) {
//            if (event.isAttack) return
//            player.stopUsingItem()
//            event.setSwingHand(false) // 很重要，防抖动
//            event.isCanceled = true
//        }
//    }
//
//    @SubscribeEvent
//    private fun preventInput(event: OnPreInputExecuteEvent.Pre) {
//        val animatable = event.holder as? IEntityAnimatable<*> ?: return
//        val isHitting = animatable.isPlayingHitAnim { !it.isCancelled }
//        val isParried = ParryAnimSkill.PARRY_SYNCED_ANIM.any { it.value.isPlaying(animatable) { !it.isCancelled } }
//        event.isCanceled = isHitting || isParried
//    }

}