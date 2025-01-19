package cn.solarmoon.spirit_of_fight.feature.hit

import cn.solarmoon.spark_core.entity.attack.AttackSystem
import cn.solarmoon.spark_core.entity.knockBackRelativeView
import net.minecraft.world.entity.Entity
import net.minecraft.world.entity.LivingEntity
import org.ode4j.ode.DContactBuffer
import org.ode4j.ode.DGeom

open class Stab(
    override val strength: AttackStrength,
    override val indefensible: Boolean
): BaseHitType() {

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
        val attacker = o1.body.owner as? Entity ?: return
        val target = o2.body.owner as? LivingEntity ?: return
        target.knockBackRelativeView(attacker, 0.15 + strength.value / 10.0)
    }

}