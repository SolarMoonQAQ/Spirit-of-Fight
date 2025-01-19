package cn.solarmoon.spirit_of_fight.feature.fight_skill.controller

import cn.solarmoon.spark_core.animation.IEntityAnimatable
import cn.solarmoon.spirit_of_fight.feature.fight_skill.skill.AttackAnimSkill
import cn.solarmoon.spirit_of_fight.feature.fight_skill.skill.CommonGuardAnimSkill
import cn.solarmoon.spirit_of_fight.feature.fight_skill.skill.DodgeAnimSkill
import cn.solarmoon.spirit_of_fight.feature.fight_skill.skill.ParryAnimSkill
import cn.solarmoon.spirit_of_fight.feature.fight_skill.skill.SpecialAttackAnimSkill
import cn.solarmoon.spirit_of_fight.registry.common.skill.SOFSwordSkills
import net.minecraft.tags.ItemTags
import net.minecraft.world.entity.LivingEntity
import org.ode4j.math.DVector3

class SwordFightSkillController(
    holder: LivingEntity,
    animatable: IEntityAnimatable<*>
): CommonFightSkillController("sword", holder, animatable, 3), IFSSwitchNextCombo {

    override val switchComboIndex: Int = 0

    override val boxLength: DVector3 = DVector3(0.65, 0.65, 1.15)
    override val boxOffset: DVector3 = DVector3(0.0, 0.0, -0.575)

    val combo1 = SOFSwordSkills.COMBO_0.get().create(animatable, this)
    val combo2 = SOFSwordSkills.COMBO_1.get().create(animatable, this)
    val combo3 = SOFSwordSkills.COMBO_2.get().create(animatable, this)
    val sprintingAttack = SOFSwordSkills.SPRINTING_ATTACK.get().create(animatable, this)
    val jumpAttack = SOFSwordSkills.JUMP_ATTACK.get().create(animatable, this)
    val guard = SOFSwordSkills.GUARD.get().create(animatable, this)
    val parry = SOFSwordSkills.PARRY.get().create(animatable, this)
    val dodge = SOFSwordSkills.DODGE.get().create(animatable, this)
    val special = SOFSwordSkills.SPECIAL_ATTACK_S.get().create(animatable, this)

    override fun isAvailable(): Boolean {
        return holder.mainHandItem.`is`(ItemTags.SWORDS)
    }

    override fun getComboSkill(index: Int): AttackAnimSkill {
        return when(index) {
            0 -> combo1
            1 -> combo2
            2 -> combo3
            else -> combo1
        }
    }

    override fun getSprintingAttackSkill(): AttackAnimSkill {
        return sprintingAttack
    }

    override fun getJumpAttackSkill(): AttackAnimSkill {
        return jumpAttack
    }

    override fun getGuardSkill(): CommonGuardAnimSkill {
        return guard
    }

    override fun getParrySkill(): ParryAnimSkill {
        return parry
    }

    override fun getDodgeSkill(): DodgeAnimSkill {
        return dodge
    }

    override fun getSpecialAttackSkill(index: Int): SpecialAttackAnimSkill {
        return special
    }

}