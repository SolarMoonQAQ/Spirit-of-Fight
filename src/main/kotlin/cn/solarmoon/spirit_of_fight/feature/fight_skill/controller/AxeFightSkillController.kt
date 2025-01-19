package cn.solarmoon.spirit_of_fight.feature.fight_skill.controller

import cn.solarmoon.spark_core.animation.IEntityAnimatable
import cn.solarmoon.spirit_of_fight.feature.fight_skill.skill.AttackAnimSkill
import cn.solarmoon.spirit_of_fight.feature.fight_skill.skill.CommonGuardAnimSkill
import cn.solarmoon.spirit_of_fight.feature.fight_skill.skill.DodgeAnimSkill
import cn.solarmoon.spirit_of_fight.feature.fight_skill.skill.ParryAnimSkill
import cn.solarmoon.spirit_of_fight.feature.fight_skill.skill.SpecialAttackAnimSkill
import cn.solarmoon.spirit_of_fight.registry.common.skill.SOFAxeSkills
import cn.solarmoon.spirit_of_fight.registry.common.skill.SOFSwordSkills
import net.minecraft.tags.ItemTags
import net.minecraft.world.entity.LivingEntity
import org.ode4j.math.DVector3

class AxeFightSkillController(
    holder: LivingEntity,
    animatable: IEntityAnimatable<*>
): CommonFightSkillController("axe", holder, animatable, 3) {

    override val boxLength: DVector3 = DVector3(0.75, 0.75, 0.75)
    override val boxOffset: DVector3 = DVector3(0.0, 0.0, -0.55)
    
    val combo0 = SOFAxeSkills.COMBO_0.get().create(animatable, this)
    val combo1 = SOFAxeSkills.COMBO_1.get().create(animatable, this)
    val combo2 = SOFAxeSkills.COMBO_2.get().create(animatable, this)
    val sprintingAttack = SOFAxeSkills.SPRINTING_ATTACK.get().create(animatable, this)
    val jumpAttack = SOFAxeSkills.JUMP_ATTACK.get().create(animatable, this)
    val guard = SOFAxeSkills.GUARD.get().create(animatable, this)
    val parry = SOFAxeSkills.PARRY.get().create(animatable, this)
    val dodge = SOFAxeSkills.DODGE.get().create(animatable, this)
    val special = SOFAxeSkills.SPECIAL_ATTACK.get().create(animatable, this)

    override fun isAvailable(): Boolean {
        return holder.mainHandItem.`is`(ItemTags.AXES)
    }

    override fun getParrySkill(): ParryAnimSkill {
        return parry
    }

    override fun getComboSkill(index: Int): AttackAnimSkill {
        return when(index) {
            0 -> combo0
            1 -> combo1
            2 -> combo2
            else -> combo0
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