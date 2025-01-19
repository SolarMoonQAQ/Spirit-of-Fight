package cn.solarmoon.spirit_of_fight.feature.fight_skill.skill

import cn.solarmoon.spark_core.animation.IEntityAnimatable
import cn.solarmoon.spark_core.animation.anim.play.AnimInstance
import cn.solarmoon.spark_core.entity.attack.AttackSystem
import cn.solarmoon.spark_core.skill.Skill
import cn.solarmoon.spark_core.skill.SkillType
import cn.solarmoon.spirit_of_fight.feature.fight_skill.spirit.getFightSpirit
import cn.solarmoon.spirit_of_fight.feature.hit.HitType
import net.minecraft.world.phys.Vec3
import org.ode4j.ode.DBody
import org.ode4j.ode.DContactBuffer
import org.ode4j.ode.DGeom

open class SpecialAttackAnimSkill(
    animatable: IEntityAnimatable<*>,
    skillType: SkillType<IEntityAnimatable<*>, out Skill<IEntityAnimatable<*>>>,
    animName: String,
    damageMultiply: Double,
    baseAttackSpeed: Double,
    switchNode: Double?,
    hitType: HitType,
    enableAttack: (AnimInstance) -> Boolean,
    enableMove: ((AnimInstance) -> Vec3?),
    onUpdate: () -> Unit = {}
): AttackAnimSkill(animatable, skillType, animName, damageMultiply, baseAttackSpeed, switchNode, hitType, enableAttack, enableMove, onUpdate) {

    override fun activate() {
        if (!entity.getFightSpirit().isFull) return
        super.activate()
    }

    override fun modifyFightSpirit(o1: DGeom, o2: DGeom, buffer: DContactBuffer, attackSystem: AttackSystem) {}

    override fun whenAttackActive(isBeginningMoment: Boolean, aBody: DBody) {
        super.whenAttackActive(isBeginningMoment, aBody)
        if (isBeginningMoment) entity.getFightSpirit().clear()
    }

}