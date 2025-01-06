package cn.solarmoon.spirit_of_fight.feature.fight_skill.controller

import cn.solarmoon.spark_core.animation.IEntityAnimatable
import cn.solarmoon.spirit_of_fight.feature.fight_skill.skill.AttackAnimSkill
import cn.solarmoon.spirit_of_fight.feature.fight_skill.skill.CommonGuardAnimSkill
import cn.solarmoon.spirit_of_fight.feature.fight_skill.skill.DodgeAnimSkill
import cn.solarmoon.spirit_of_fight.feature.fight_skill.skill.ParryAnimSkill
import cn.solarmoon.spirit_of_fight.registry.common.SOFSkills
import net.minecraft.tags.ItemTags
import net.minecraft.world.entity.LivingEntity
import net.neoforged.neoforge.event.entity.living.LivingIncomingDamageEvent
import org.ode4j.math.DVector3

class SwordFightSkillController(
    holder: LivingEntity,
    animatable: IEntityAnimatable<*>
): FightSkillController(holder, animatable) {

    override val boxLength: DVector3 = DVector3(0.65, 0.65, 1.15)
    override val boxOffset: DVector3 = DVector3(0.0, 0.0, -0.575)

    val combo1 = SOFSkills.SWORD_COMBO_0.get().create(animatable)
    val combo2 = SOFSkills.SWORD_COMBO_1.get().create(animatable)
    val combo3 = SOFSkills.SWORD_COMBO_2.get().create(animatable)
    val sprintingAttack = SOFSkills.SWORD_SPRINTING_ATTACK.get().create(animatable)
    val jumpAttack = SOFSkills.SWORD_JUMP_ATTACK.get().create(animatable)
    val guard = SOFSkills.SWORD_GUARD.get().create(animatable)
    val parry = SOFSkills.SWORD_PARRY.get().create(animatable)
    val dodge = SOFSkills.COMMON_DODGE.get().create(animatable)

    init {
        addSkill(combo1)
        addSkill(combo2)
        addSkill(combo3)
        addSkill(sprintingAttack)
        addSkill(jumpAttack)
        addSkill(guard)
        addSkill(parry)
        addSkill(dodge)
    }

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

    fun getParrySkill(): ParryAnimSkill {
        return parry
    }

    override fun getDodgeSkill(): DodgeAnimSkill {
        return dodge
    }

    override fun onHurt(event: LivingIncomingDamageEvent) {
        super.onHurt(event)
        parry.onHurt(event)
    }

}