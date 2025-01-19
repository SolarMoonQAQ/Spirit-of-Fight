package cn.solarmoon.spirit_of_fight.feature.fight_skill.controller

import cn.solarmoon.spark_core.animation.IEntityAnimatable
import cn.solarmoon.spirit_of_fight.feature.fight_skill.skill.ParryAnimSkill
import net.minecraft.world.entity.LivingEntity
import net.neoforged.neoforge.event.entity.living.LivingIncomingDamageEvent

abstract class CommonFightSkillController(
    name: String,
    holder: LivingEntity,
    animatable: IEntityAnimatable<*>,
    maxComboAmount: Int
): FightSkillController(name, holder, animatable, maxComboAmount) {

    abstract fun getParrySkill(): ParryAnimSkill

    override fun onHurt(event: LivingIncomingDamageEvent) {
        super.onHurt(event)
        getParrySkill().onHurt(event)
    }

}