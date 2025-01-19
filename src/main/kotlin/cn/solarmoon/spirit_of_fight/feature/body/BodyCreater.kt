package cn.solarmoon.spirit_of_fight.feature.body

import cn.solarmoon.spark_core.animation.IEntityAnimatable
import cn.solarmoon.spark_core.entity.attack.AttackSystem
import cn.solarmoon.spark_core.phys.AttackCallBack
import cn.solarmoon.spark_core.phys.BodyType
import cn.solarmoon.spark_core.phys.createAnimatedPivotBody
import cn.solarmoon.spark_core.phys.createEntityAnimatedAttackBody
import cn.solarmoon.spark_core.skill.getSkillController
import cn.solarmoon.spirit_of_fight.feature.fight_skill.skill.AttackAnimSkill
import net.minecraft.world.level.Level
import org.ode4j.ode.DBody
import org.ode4j.ode.DContactBuffer
import org.ode4j.ode.DGeom

fun createGuardAnimBody(boneName: String, type: BodyType, owner: IEntityAnimatable<*>, level: Level, provider: DBody.() -> Unit = {}) =
    createAnimatedPivotBody(boneName, type, owner, level) {

        disable()

        provider.invoke(this)
    }

fun createSkillAttackAnimBody(boneName: String, type: BodyType, owner: IEntityAnimatable<*>, level: Level, attackSystem: AttackSystem, provider: DBody.() -> Unit = {}) =
    createEntityAnimatedAttackBody(boneName, type, owner, level, object : AttackCallBack(attackSystem) {

        val entity get() = owner.animatable

        override fun whenAboutToAttack(o1: DGeom, o2: DGeom, buffer: DContactBuffer) {
            entity.getSkillController()?.allActiveSkills?.forEach {
                if (it is AttackAnimSkill) {
                    it.whenAboutToAttack(o1, o2, buffer, attackSystem)
                }
            }
        }

        override fun whenTargetAttacked(firstAttack: Boolean, o1: DGeom, o2: DGeom, buffer: DContactBuffer) {
            entity.getSkillController()?.allActiveSkills?.forEach {
                if (it is AttackAnimSkill) {
                    it.whenTargetAttacked(firstAttack, o1, o2, buffer, attackSystem)
                }
            }
        }
    }) {


        provider.invoke(this)
    }