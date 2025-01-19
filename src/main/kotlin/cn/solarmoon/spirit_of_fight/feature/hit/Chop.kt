package cn.solarmoon.spirit_of_fight.feature.hit

import cn.solarmoon.spark_core.animation.IAnimatable
import cn.solarmoon.spark_core.animation.anim.play.AnimInstance
import cn.solarmoon.spark_core.entity.attack.AttackSystem
import cn.solarmoon.spark_core.util.Side
import org.ode4j.ode.DContactBuffer
import org.ode4j.ode.DGeom

open class Chop(
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

    }

}