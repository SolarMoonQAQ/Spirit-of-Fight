package cn.solarmoon.spirit_of_fight.feature.fight_skill.skill

import cn.solarmoon.spark_core.animation.IEntityAnimatable
import cn.solarmoon.spark_core.animation.anim.play.AnimInstance
import cn.solarmoon.spark_core.entity.getLateralSide
import cn.solarmoon.spark_core.entity.knockBackRelativeView
import cn.solarmoon.spark_core.skill.Skill
import cn.solarmoon.spark_core.skill.SkillType
import cn.solarmoon.spark_core.util.Side
import cn.solarmoon.spirit_of_fight.feature.fight_skill.sync.ClientOperationPayload
import cn.solarmoon.spirit_of_fight.fighter.getPatch
import net.minecraft.world.effect.MobEffectInstance
import net.minecraft.world.effect.MobEffects
import net.minecraft.world.entity.Entity
import net.minecraft.world.entity.LivingEntity
import net.minecraft.world.entity.player.Player
import net.minecraft.world.phys.Vec3
import net.neoforged.neoforge.event.entity.living.LivingIncomingDamageEvent
import net.neoforged.neoforge.network.PacketDistributor
import kotlin.math.PI
import kotlin.math.cos
import kotlin.math.sin

open class ParryAnimSkill(
    animatable: IEntityAnimatable<*>,
    skillType: SkillType<IEntityAnimatable<*>, out Skill<IEntityAnimatable<*>>>,
    animName: String,
    guardRange: Double,
    private val enableParry: (AnimInstance) -> Boolean
): GuardAnimSkill(animatable, skillType, animName, guardRange) {

    override fun onActivate() {
        holder.animController.setAnimation(animName, 0) {
            shouldTurnBody = true
            onTick {
                if (isActive()) {
                    if (enableParry.invoke(this)) entity.getPatch().weaponGuardBody?.enable()
                    else entity.getPatch().weaponGuardBody?.disable()

                    entity.getPatch().operateFreeze = true
                    entity.getPatch().moveInputFreeze = true

                    if (isCancelled) end()
                }
            }

            onSwitch {
                end()
            }
        }
    }

    override fun onUpdate() {

    }

    override fun onSuccessGuard(attackerPos: Vec3, event: LivingIncomingDamageEvent): Boolean {
        event.source.directEntity?.let {
            parry(attackerPos, it, event)
        }
        return true
    }

    open fun parry(attackerPos: Vec3, attacker: Entity, event: LivingIncomingDamageEvent) {
        val side = entity.getLateralSide(attackerPos, true)
        val animName = "common:parried_$side"
        if (attacker is IEntityAnimatable<*> && attacker.animations.hasAnimation(animName)) {
            playParriedAnim(side, attacker)
            when(side) {
                Side.LEFT -> PacketDistributor.sendToAllPlayers(ClientOperationPayload(entity.id, "parried_left", Vec3.ZERO, attacker.id))
                else -> PacketDistributor.sendToAllPlayers(ClientOperationPayload(entity.id, "parried_right", Vec3.ZERO, attacker.id))
            }
        } else if (attacker is LivingEntity) {
            attacker.addEffect(MobEffectInstance(MobEffects.MOVEMENT_SLOWDOWN, 20, 9, false, false, false))
            attacker.knockBackRelativeView(entity, 0.5)
        }
    }

    fun playParriedAnim(side: Side, attacker: IEntityAnimatable<*>) {
        attacker.animController.setAnimation("common:parried_$side", 0) {
            onTick {
                val patch = attacker.animatable.getPatch()
                patch.moveInputFreeze = true
                patch.preInputFreeze = true
                patch.operateFreeze = true
            }
        }
    }

}