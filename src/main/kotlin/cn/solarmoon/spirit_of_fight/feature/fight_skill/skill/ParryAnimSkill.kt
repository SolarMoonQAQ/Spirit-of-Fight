package cn.solarmoon.spirit_of_fight.feature.fight_skill.skill

import cn.solarmoon.spark_core.SparkCore
import cn.solarmoon.spark_core.animation.IEntityAnimatable
import cn.solarmoon.spark_core.animation.anim.play.MixedAnimation
import cn.solarmoon.spark_core.entity.state.getLateralSide
import cn.solarmoon.spark_core.skill.Skill
import cn.solarmoon.spark_core.skill.SkillType
import cn.solarmoon.spark_core.util.Side
import cn.solarmoon.spirit_of_fight.feature.fight_skill.sync.ClientOperationPayload
import cn.solarmoon.spirit_of_fight.fighter.getPatch
import net.minecraft.world.effect.MobEffectInstance
import net.minecraft.world.effect.MobEffects
import net.minecraft.world.entity.Entity
import net.minecraft.world.entity.LivingEntity
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
    private val enableParry: (MixedAnimation) -> Boolean
): GuardAnimSkill(animatable, skillType, animName, guardRange) {

    override fun onActivate() {
        holder.animController.stopAndAddAnimation(MixedAnimation(animName, startTransSpeed = 6f).apply { shouldTurnBody = true })
    }

    override fun onUpdate() {
        holder.animData.playData.getMixedAnimation(animName)?.let {
            if (enableParry.invoke(it)) entity.getPatch().weaponGuardBody?.enable()
            else entity.getPatch().weaponGuardBody?.disable()

            if (it.isCancelled) end()
        } ?: run {
            end()
        }
    }

    override fun onSuccessGuard(attackerPos: Vec3, event: LivingIncomingDamageEvent): Boolean {
        event.source.entity?.let {
            parry(attackerPos, it, event)
        }
        return true
    }

    open fun parry(attackerPos: Vec3, attacker: Entity, event: LivingIncomingDamageEvent) {
        val side = entity.getLateralSide(attackerPos, true)
        val animName = "parried_$side"
        if (attacker is IEntityAnimatable<*> && attacker.animData.animationSet.hasAnimation(animName)) {
            val flag = if (side == Side.LEFT) 0 else 1
            playParriedAnim(flag)
            PacketDistributor.sendToAllPlayers(ClientOperationPayload(attacker.id, "parried", Vec3.ZERO, flag))
        } else if (attacker is LivingEntity) {
            attacker.addEffect(MobEffectInstance(MobEffects.MOVEMENT_SLOWDOWN, 20, 9, false, false, false))
            attacker.knockback(0.5, sin(entity.yRot * (PI / 180.0)), -cos(entity.yRot * (PI / 180.0)))
        }
    }

    open fun playParriedAnim(flag: Int) {
        val side = if (flag == 0) Side.LEFT else Side.RIGHT
        holder.animController.stopAndAddAnimation(MixedAnimation("parried_$side", startTransSpeed = 6f))
    }

}