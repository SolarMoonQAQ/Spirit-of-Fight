package cn.solarmoon.spirit_of_fight.feature.fight_skill.attack

import cn.solarmoon.spark_core.SparkCore
import cn.solarmoon.spark_core.animation.preset_anim.CommonState
import cn.solarmoon.spark_core.animation.preset_anim.EntityStates
import cn.solarmoon.spark_core.entity.attack.AttackSystem
import cn.solarmoon.spark_core.entity.attack.AttackedData
import cn.solarmoon.spark_core.entity.attack.pushAttackedData
import cn.solarmoon.spark_core.entity.attack.updateAttackedData
import cn.solarmoon.spark_core.event.ChangePresetAnimEvent
import cn.solarmoon.spark_core.event.OnBodyCreateEvent
import cn.solarmoon.spark_core.event.PlayerGetAttackStrengthEvent
import cn.solarmoon.spark_core.phys.createEntityBoundingBoxBody
import cn.solarmoon.spark_core.registry.common.SparkBodyTypes
import cn.solarmoon.spark_core.skill.getSkillController
import cn.solarmoon.spark_core.skill.getTypedSkillController
import cn.solarmoon.spirit_of_fight.feature.fight_skill.controller.FightSkillController
import cn.solarmoon.spirit_of_fight.feature.fight_skill.controller.HammerFightSkillController
import cn.solarmoon.spirit_of_fight.feature.fight_skill.skill.AttackAnimSkill
import cn.solarmoon.spirit_of_fight.feature.hit.setHitType
import cn.solarmoon.spirit_of_fight.fighter.getPatch
import cn.solarmoon.spirit_of_fight.registry.common.SOFRegistries
import cn.solarmoon.spirit_of_fight.registry.common.SOFTypedAnimations
import net.minecraft.client.player.LocalPlayer
import net.minecraft.world.entity.LivingEntity
import net.minecraft.world.entity.animal.IronGolem
import net.minecraft.world.entity.player.Player
import net.neoforged.bus.api.SubscribeEvent
import net.neoforged.neoforge.event.entity.EntityJoinLevelEvent
import net.neoforged.neoforge.event.entity.living.LivingIncomingDamageEvent
import net.neoforged.neoforge.event.entity.living.LivingKnockBackEvent
import net.neoforged.neoforge.event.entity.player.CriticalHitEvent
import net.neoforged.neoforge.event.entity.player.SweepAttackEvent
import net.neoforged.neoforge.event.tick.EntityTickEvent

object AttackModifier {

    /**
     * 原版横扫从此再见
     */
    @SubscribeEvent
    private fun playerSweep(event: SweepAttackEvent) {
        val player = event.entity
        if (player.getPatch().isAttacking) {
            event.isSweeping = false
        }
    }

    /**
     * 使能够兼容别的模组的暴击修改，并且把原版跳劈删去
     */
    @SubscribeEvent
    private fun playerCriticalHit(event: CriticalHitEvent) {
        val player = event.entity
        if (player.getPatch().isAttacking) {
            // 逻辑是原版暴击只能在跳劈情况下触发，因此直接删掉原版跳劈，但是别的模组由暴击率驱动的概率性伤害显然理应不受其影响
            if (event.vanillaMultiplier == 1.5f) event.isCriticalHit = false
        }
    }

    /**
     * 攻击时取消原版的攻击间隔对攻击的削弱
     */
    @SubscribeEvent
    private fun cancelAttackDuration(event: PlayerGetAttackStrengthEvent) {
        val entity = event.entity
        val sc = entity.getTypedSkillController<FightSkillController>() ?: return
        if (sc.isPlaying()) {
            event.attackStrengthScale = 1f
        }
    }

    /**
     * 攻击时根据技能倍率增加攻击伤害
     */
    @SubscribeEvent
    private fun modifyAttackStrength(event: LivingIncomingDamageEvent) {
        val entity = event.source.entity ?: return
        val sc = entity.getSkillController() ?: return
        (sc.firstActiveSkill as? AttackAnimSkill)?.getDamageMultiply()?.let {
            event.container.newDamage *= it.toFloat()
        }
    }

    /**
     * 取消默认情况下击退的y轴向量
     */
    @SubscribeEvent
    private fun knockBackModify(event: LivingKnockBackEvent) {
        val entity = event.entity
        entity.setOnGround(false)
    }

    @SubscribeEvent
    private fun test(event: EntityJoinLevelEvent) {
        val entity = event.entity
        if (entity is IronGolem) {
            createEntityBoundingBoxBody(SparkBodyTypes.ENTITY_BOUNDING_BOX.get(), entity, event.level) {
                val system = AttackSystem(entity).apply { ignoreInvulnerableTime = false }
                firstGeom.onCollide { o2, buffer ->
                    system.commonGeomAttack(firstGeom, o2) {
                        val target = o2.body.owner as? LivingEntity ?: return@commonGeomAttack
                        target.pushAttackedData(AttackedData(firstGeom, o2.body))
                        target.updateAttackedData { setHitType(SOFRegistries.HIT_TYPE.toList().random()) }
                    }
                    system.reset()
                }
            }
        }
    }

    @SubscribeEvent
    private fun test0(event: OnBodyCreateEvent) {
        val body = event.body
        if (body.type == SparkBodyTypes.ENTITY_BOUNDING_BOX.get() && body.owner is Player) body.destroy()
    }

    @SubscribeEvent
    private fun test3(event: EntityTickEvent.Pre) {
        val entity = event.entity
        //SparkCore.LOGGER.info((entity as IAttackDataPusher).data.toString())
    }

    @SubscribeEvent
    private fun test2(event: ChangePresetAnimEvent.PlayerState) {
        val player = event.player
        if (player.isLocalPlayer && player is LocalPlayer) {
            if (player.getSkillController() is HammerFightSkillController) {
                when(event.state) {
                    EntityStates.Idle -> event.newAnim = SOFTypedAnimations.HAMMER_IDLE.get()
                    EntityStates.Walk -> event.newAnim = SOFTypedAnimations.HAMMER_WALK.get()
                    EntityStates.WalkBack -> event.newAnim = SOFTypedAnimations.HAMMER_WALK_BACK.get()
                    EntityStates.Sprinting -> event.newAnim = SOFTypedAnimations.HAMMER_SPRINTING.get()
                    EntityStates.Fall -> event.newAnim = SOFTypedAnimations.HAMMER_FALL.get()
                }
            }
        }
    }

    @SubscribeEvent
    private fun test3(event: ChangePresetAnimEvent.Common) {
        val entity = event.entity
        if (entity.getSkillController() is HammerFightSkillController) {
            when(event.commonState) {
                CommonState.JUMP -> event.newAnim = "Common/hammer_jump_start"
            }
        }
    }

}