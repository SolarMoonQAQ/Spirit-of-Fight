package cn.solarmoon.spirit_of_fight.fighter.player

import cn.solarmoon.spark_core.SparkCore
import cn.solarmoon.spark_core.entity.preinput.PreInput
import cn.solarmoon.spark_core.entity.preinput.getPreInput
import cn.solarmoon.spark_core.event.OnPreInputExecuteEvent
import cn.solarmoon.spark_core.local_control.LocalInputController
import cn.solarmoon.spark_core.skill.getAllSkillControllers
import cn.solarmoon.spark_core.skill.getSkillController
import cn.solarmoon.spark_core.skill.getTypedSkillController
import cn.solarmoon.spark_core.util.MoveDirection
import cn.solarmoon.spirit_of_fight.feature.fight_skill.controller.CommonFightSkillController
import cn.solarmoon.spirit_of_fight.feature.fight_skill.controller.FightSkillController
import cn.solarmoon.spirit_of_fight.feature.fight_skill.controller.HammerFightSkillController
import cn.solarmoon.spirit_of_fight.feature.fight_skill.controller.IFSSwitchNextCombo
import cn.solarmoon.spirit_of_fight.feature.fight_skill.controller.SwordFightSkillController
import cn.solarmoon.spirit_of_fight.feature.fight_skill.spirit.getFightSpirit
import cn.solarmoon.spirit_of_fight.feature.fight_skill.sync.ClientOperationPayload
import cn.solarmoon.spirit_of_fight.fighter.getPatch
import cn.solarmoon.spirit_of_fight.registry.client.SOFKeyMappings
import net.minecraft.client.Minecraft
import net.minecraft.client.player.Input
import net.minecraft.client.player.LocalPlayer
import net.minecraft.world.entity.player.Player
import net.minecraft.world.phys.HitResult
import net.minecraft.world.phys.Vec3
import net.neoforged.bus.api.SubscribeEvent
import net.neoforged.neoforge.client.event.InputEvent
import net.neoforged.neoforge.client.event.MovementInputUpdateEvent
import kotlin.math.PI
import kotlin.math.atan2

object PlayerLocalController: LocalInputController() {

    var jumpContainTick = 0
    var guardContainTick = 0
    val attackKey get() = Minecraft.getInstance().options.keyAttack

    override fun laterInit() {
        addTickingKey(attackKey)
        addTickingKey(SOFKeyMappings.GUARD)
        addTickingKey(SOFKeyMappings.PARRY)
        addTickingKey(SOFKeyMappings.SPECIAL_ATTACK)
    }

    fun attack(player: LocalPlayer, skillController: FightSkillController) {
        val preInput = player.getPreInput()
        when {
            jumpContainTick < 0 -> {
                jumpContainTick = 0
                preInput.setInput("jump_attack", 5) {
                    skillController.getJumpAttackSkill().activate()
                    sendPackage(ClientOperationPayload(player.id, preInput.id, Vec3.ZERO, 0))
                }
            }
            player.isSprinting -> {
                preInput.setInput("sprinting_attack", 5) {
                    skillController.getSprintingAttackSkill().activate()
                    sendPackage(ClientOperationPayload(player.id, preInput.id, Vec3.ZERO, 0))
                }
            }
            else -> {
                preInput.setInput("combo", 5) {
                    if (!skillController.isAvailable()) return@setInput
                    skillController.comboIndex.increment()
                    skillController.getComboSkill().activate()
                    sendPackage(ClientOperationPayload(player.id, preInput.id, Vec3.ZERO, skillController.comboIndex.get()))
                }
            }
        }
    }

    fun specialAttackPressOnce(player: LocalPlayer, skillController: FightSkillController) {
        val preInput = player.getPreInput()

        when(skillController) {
            is HammerFightSkillController -> {
                if (skillController.comboIndex.get() == 0) {
                    preInput.setInput("combo_change", 5) {
                        skillController.setComboChange()
                        skillController.getComboSkill().activate()
                        sendPackage(ClientOperationPayload(player.id, preInput.id, Vec3.ZERO, skillController.comboIndex.get()))
                    }
                } else {
                    preInput.setInput("special_attack", 5) {
                        skillController.getSpecialAttackSkill(0).activate()
                        sendPackage(ClientOperationPayload(player.id, preInput.id, Vec3.ZERO, 0))
                    }
                }
            }
            else -> {
                preInput.setInput("special_attack", 5) {
                    skillController.getSpecialAttackSkill(0).activate()
                    sendPackage(ClientOperationPayload(player.id, preInput.id, Vec3.ZERO, 0))
                }
            }
        }
    }

    fun specialAttackPressHold(holdTime: Int, player: LocalPlayer, skillController: FightSkillController) {
        if (player.getFightSpirit().isFull) {
            val preInput = player.getPreInput()

            when(skillController) {
                is SwordFightSkillController -> {

                }
            }
        }
    }

    fun specialAttackRelease(holdTime: Int, player: LocalPlayer, skillController: FightSkillController) {
        if (player.getFightSpirit().isFull) {
            val preInput = player.getPreInput()

            when(skillController) {
                is SwordFightSkillController -> {

                }
            }
        }
    }

    fun guard(player: LocalPlayer, skillController: FightSkillController) {
        if (skillController.getGuardSkill().isActive()) return

        val preInput = player.getPreInput()
        preInput.setInput("guard", 5) {
            skillController.getGuardSkill().activate()
            sendPackage(ClientOperationPayload(player.id, preInput.id, Vec3.ZERO, 0))
        }
    }

    fun guardStop(player: LocalPlayer, skillController: FightSkillController) {
        if (skillController.getGuardSkill().isStanding) {
            skillController.getGuardSkill().end()
            sendPackage(ClientOperationPayload(player.id, "guard_stop", Vec3.ZERO, 0))
        }
    }

    fun dodge(player: LocalPlayer, skillController: FightSkillController, input: Input) {
        val dodge = skillController.getDodgeSkill()
        val preInput = player.getPreInput()
        if (player.onGround()) {
            val camera = Minecraft.getInstance().gameRenderer.mainCamera
            val angle = atan2(input.moveVector.y, -input.moveVector.x) - PI.toFloat() / 2
            val v = Vec3.directionFromRotation(0f, camera.yRot).yRot(angle)
            val d = MoveDirection.getByInput(input) ?: return
            preInput.setInput("dodge", 5) {
                dodge.moveVector = v
                dodge.direction = d
                dodge.activate()
                sendPackage(ClientOperationPayload(player.id, preInput.id, v, d.id))
            }
        }
    }

    fun parry(player: LocalPlayer, skillController: CommonFightSkillController) {
        if (!skillController.getGuardSkill().isBacking && skillController.getGuardSkill().isActive()) {
            skillController.getParrySkill().activate()
            sendPackage(ClientOperationPayload(player.id, "parry", Vec3.ZERO, 0))
        }
    }

    override fun tick(player: LocalPlayer, input: Input) {
        if (player.jumping) {
            jumpContainTick = -10
        } else jumpContainTick++

        val skillController = player.getTypedSkillController<FightSkillController>() ?: return

        onRelease(attackKey) {
            if (shouldAttack(player)) {
                attack(player, skillController)
            }
        }

        onPressOnce(SOFKeyMappings.SPECIAL_ATTACK) {
            specialAttackPressOnce(player, skillController)
        }

        onPress(SOFKeyMappings.SPECIAL_ATTACK) {
            specialAttackPressHold(it, player, skillController)
        }

        onRelease(SOFKeyMappings.SPECIAL_ATTACK) {
            specialAttackRelease(it, player, skillController)
        }

        onPress(SOFKeyMappings.GUARD) {
            guard(player, skillController)
        }

        if (!SOFKeyMappings.GUARD.isDown) {
            guardContainTick++
            if (guardContainTick > 0) guardStop(player, skillController)
        } else guardContainTick = -3

        onPressOnce(SOFKeyMappings.PARRY) {
            if (skillController is CommonFightSkillController) {
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

        // 对可变招技能管理器的预输入控制
        if (skillController is IFSSwitchNextCombo && skillController.getComboSkill(skillController.switchComboIndex).runTime in skillController.inputWindow) preInput.executeIfPresent("combo")
    }

    override fun onInteract(player: LocalPlayer, event: InputEvent.InteractionKeyMappingTriggered) {
        if (event.isAttack && shouldAttack(player) || player.getPatch().operateFreeze) {
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

        // 播放指定技能时禁止移动
        if (player.getPatch().moveInputFreeze) {
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

    @SubscribeEvent
    private fun onPreInputExecute(event: OnPreInputExecuteEvent.Pre) {
        val entity = event.holder
        if (entity.getPatch().preInputFreeze == true) event.isCanceled = true
    }

}