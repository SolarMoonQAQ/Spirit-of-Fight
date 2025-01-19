package cn.solarmoon.spirit_of_fight.feature.fight_skill.controller

import cn.solarmoon.spark_core.animation.IEntityAnimatable
import cn.solarmoon.spark_core.phys.toDVector3
import cn.solarmoon.spirit_of_fight.data.SOFItemTags
import cn.solarmoon.spirit_of_fight.feature.fight_skill.skill.AttackAnimSkill
import cn.solarmoon.spirit_of_fight.feature.fight_skill.skill.CommonGuardAnimSkill
import cn.solarmoon.spirit_of_fight.feature.fight_skill.skill.DodgeAnimSkill
import cn.solarmoon.spirit_of_fight.feature.fight_skill.skill.SpecialAttackAnimSkill
import cn.solarmoon.spirit_of_fight.fighter.getPatch
import cn.solarmoon.spirit_of_fight.registry.common.skill.SOFHammerSkills
import net.minecraft.world.entity.LivingEntity
import org.ode4j.math.DVector3
import org.ode4j.ode.DBox

class HammerFightSkillController(
    holder: LivingEntity,
    animatable: IEntityAnimatable<*>
): HeavyFightSkillController("hammer", holder, animatable, 3) {

    override val boxLength: DVector3 = DVector3(0.75, 1.25, 0.75)
    override val boxOffset: DVector3 = DVector3(0.0, 0.0, -1.125)

    val combo1 = SOFHammerSkills.COMBO_0.get().create(animatable, this)
    val combo2 = SOFHammerSkills.COMBO_1.get().create(animatable, this)
    val combo3 = SOFHammerSkills.COMBO_2.get().create(animatable, this)
    val comboC1 = SOFHammerSkills.COMBO_C1.get().create(animatable, this)
    val comboC2 = SOFHammerSkills.COMBO_C2.get().create(animatable, this)
    val sprintingAttack = SOFHammerSkills.SPRINTING_ATTACK.get().create(animatable, this)
    val jumpAttack = SOFHammerSkills.JUMP_ATTACK.get().create(animatable, this)
    val dodge = SOFHammerSkills.DODGE.get().create(animatable, this)
    val guard = SOFHammerSkills.GUARD.get().create(animatable, this)
    val special = SOFHammerSkills.SPECIAL_ATTACK.get().create(animatable, this)

    override fun isAvailable(): Boolean {
        return holder.mainHandItem.`is`(SOFItemTags.FORGE_HAMMER)
    }

    override fun tick() {
        super.tick()
        if (guard.isActive()) {
            (holder.getPatch().weaponGuardBody?.firstGeom as? DBox)?.apply {
                lengths = DVector3(0.65, 0.65, 2.0)
                offsetPosition = DVector3(0.0, 0.0, -0.5)
            }
        }

        if (comboC1.isActive()) {
            (holder.getPatch().weaponAttackBody?.firstGeom as? DBox)?.apply {
                body.position = animatable.getWorldBonePivot("rightArm").toDVector3()
                lengths = DVector3(1.0, 1.0, 1.0)
                offsetPosition = DVector3()
            }
        }
    }

    override fun getComboSkill(index: Int): AttackAnimSkill {
        return when (index) {
            0 -> if (comboChanging) comboC1 else combo1
            1 -> if (comboChanging) comboC2 else combo2
            2 -> combo3
            else -> combo1
        }
    }

    override fun getGuardSkill(): CommonGuardAnimSkill {
        return guard
    }

    override fun getSprintingAttackSkill(): AttackAnimSkill {
        return sprintingAttack
    }

    override fun getJumpAttackSkill(): AttackAnimSkill {
        return jumpAttack
    }

    override fun getDodgeSkill(): DodgeAnimSkill {
        return dodge
    }

    override fun getSpecialAttackSkill(index: Int): SpecialAttackAnimSkill {
        return special
    }

}