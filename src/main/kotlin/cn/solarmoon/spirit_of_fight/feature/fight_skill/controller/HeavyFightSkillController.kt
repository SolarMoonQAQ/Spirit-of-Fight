package cn.solarmoon.spirit_of_fight.feature.fight_skill.controller

import cn.solarmoon.spark_core.animation.IEntityAnimatable
import net.minecraft.world.entity.LivingEntity

abstract class HeavyFightSkillController(
    name: String,
    holder: LivingEntity,
    animatable: IEntityAnimatable<*>,
    maxComboAmount: Int
): FightSkillController(name, holder, animatable, maxComboAmount) {



}