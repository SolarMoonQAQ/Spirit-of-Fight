package cn.solarmoon.spirit_of_fight.feature.hit

import cn.solarmoon.spark_core.SparkCore
import cn.solarmoon.spark_core.animation.IAnimatable
import cn.solarmoon.spark_core.animation.anim.play.AnimInstance
import cn.solarmoon.spark_core.entity.attack.AttackSystem
import cn.solarmoon.spark_core.entity.knockBackRelativeView
import cn.solarmoon.spark_core.util.Side
import cn.solarmoon.spirit_of_fight.registry.common.SOFHitTypes
import net.minecraft.world.entity.Entity
import net.minecraft.world.entity.LivingEntity
import net.minecraft.world.phys.Vec3
import org.ode4j.ode.DContactBuffer
import org.ode4j.ode.DGeom

open class Upstroke(
    override val strength: AttackStrength,
    override val indefensible: Boolean
): BaseHitType() {

    override fun getHitAnimation(
        target: IAnimatable<*>,
        strength: AttackStrength,
        boneName: String,
        posSide: Side,
        hitSide: Side
    ): AnimInstance? {
        if (boneName == "head" && posSide == Side.FRONT) return super.getHitAnimation(target, strength, boneName, posSide, hitSide)
        return when(strength) {
            AttackStrength.LIGHT -> SOFHitTypes.LIGHT_CHOP.get().getHitAnimation(target, strength, boneName, posSide, hitSide)
            AttackStrength.HEAVY -> SOFHitTypes.HEAVY_CHOP.get().getHitAnimation(target, strength, boneName, posSide, hitSide)
            AttackStrength.SUPER_HEAVY -> SOFHitTypes.KNOCKDOWN_CHOP.get().getHitAnimation(target, strength, boneName, posSide, hitSide)
        }
    }

    override fun whenAboutToAttack(
        o1: DGeom,
        o2: DGeom,
        buffer: DContactBuffer,
        attackSystem: AttackSystem
    ) {
    }

    override fun whenTargetAttacked(
        firstAttacked: Boolean,
        o1: DGeom,
        o2: DGeom,
        buffer: DContactBuffer,
        attackSystem: AttackSystem
    ) {
        if (!firstAttacked) return
        val attacker = o1.body.owner as? Entity ?: return
        val target = o2.body.owner as? LivingEntity ?: return
        target.knockBackRelativeView(attacker, 0.15)
        target.addDeltaMovement(Vec3(0.0, strength.value.toDouble() / 3, 0.0))
    }

}