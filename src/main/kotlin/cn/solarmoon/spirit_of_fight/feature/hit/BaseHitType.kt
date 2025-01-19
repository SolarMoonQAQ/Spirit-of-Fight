package cn.solarmoon.spirit_of_fight.feature.hit

import cn.solarmoon.spark_core.animation.IAnimatable
import cn.solarmoon.spark_core.animation.anim.play.AnimInstance
import cn.solarmoon.spark_core.util.Side
import cn.solarmoon.spirit_of_fight.fighter.getPatch
import net.minecraft.world.entity.Entity

abstract class BaseHitType: HitType {

    override fun getHitAnimation(
        target: IAnimatable<*>,
        strength: AttackStrength,
        boneName: String,
        posSide: Side,
        hitSide: Side
    ): AnimInstance? {
        val suffix = when (posSide) {
            Side.FRONT -> when (boneName) {
                "head" -> "$simpleName:head_$hitSide"
                "waist", "leftArm", "rightArm" -> "$simpleName:body_$hitSide"
                "leftLeg", "rightLeg" -> "$simpleName:leg_$hitSide"
                else -> null
            }
            else -> when (boneName) {
                "head", "waist", "leftArm", "rightArm" -> "${strength.toString().lowercase()}_all:upperbody_$posSide"
                "leftLeg", "rightLeg" -> "${strength.toString().lowercase()}_all:lowerbody_$posSide"
                else -> null
            }
        } ?: "$simpleName:${boneName}_$posSide#$hitSide"
        val animName = suffix.let { "Hit/$it" }
        return target.animations.getAnimation(animName)?.let { AnimInstance(target, animName, it).apply {
            rejectNewAnim = { indefensible || it?.name?.substringBefore("/") != "Hit" }

            onTick {
                (holder as? Entity)?.getPatch()?.let {
                    it.moveInputFreeze = true
                    it.preInputFreeze = true
                    it.operateFreeze = true
                }
            }
        } }
    }

    override fun equals(other: Any?): Boolean {
        return (other as? HitType)?.registryKey == registryKey
    }

    override fun hashCode(): Int {
        return registryKey.hashCode()
    }

}