package cn.solarmoon.spirit_of_fight.entity

import cn.solarmoon.spark_core.util.MoveDirection
import cn.solarmoon.spirit_of_fight.entity.grab.GrabManager
import cn.solarmoon.spirit_of_fight.poise_system.PoiseData
import cn.solarmoon.spirit_of_fight.skill.tree.SkillTreeSet

interface IEntityPatch {

    var moveDirection: MoveDirection?

    var currentSkillSet: SkillTreeSet?

    val poise: PoiseData

    val grabManager: GrabManager

    var isHitting: Boolean

    var isKnockedDown: Boolean

    var chargingTime: Int

    var wieldStyle: WieldStyle

}