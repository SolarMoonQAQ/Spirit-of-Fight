package cn.solarmoon.spirit_of_fight.feature.body

import cn.solarmoon.spark_core.animation.IEntityAnimatable
import cn.solarmoon.spark_core.entity.attack.AttackSystem
import cn.solarmoon.spark_core.phys.attached_body.EntityAnimatedAttackBody
import cn.solarmoon.spark_core.skill.getSkillController
import cn.solarmoon.spirit_of_fight.feature.fight_skill.skill.AttackAnimSkill
import net.minecraft.world.level.Level
import org.ode4j.ode.DContactBuffer
import org.ode4j.ode.DGeom

open class SkillAttackAnimBody(
    bodyName: String,
    boneName: String,
    level: Level,
    animatable: IEntityAnimatable<*>,
    attackSystem: AttackSystem
): EntityAnimatedAttackBody(bodyName, boneName, level, animatable, attackSystem) {

    override fun whenAttacked(o2: DGeom, buffer: DContactBuffer) {
        super.whenAttacked(o2, buffer)

        entity.getSkillController()?.allActiveSkills?.forEach {
            if (it is AttackAnimSkill) {
                it.whenAttacked(geom, o2, buffer, attackSystem)
            }
        }
    }

}