package cn.solarmoon.spirit_of_fight.feature.fight_skill.skill

import cn.solarmoon.spark_core.skill.BaseSkill
import cn.solarmoon.spark_core.skill.Skill
import cn.solarmoon.spark_core.skill.SkillType

abstract class FightSkill<T>(
    holder: T,
    skillType: SkillType<T, out Skill<T>>
): BaseSkill<T>(holder, skillType) {



}