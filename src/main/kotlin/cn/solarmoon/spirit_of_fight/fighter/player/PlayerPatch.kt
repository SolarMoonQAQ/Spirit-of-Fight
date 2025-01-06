package cn.solarmoon.spirit_of_fight.fighter.player

import cn.solarmoon.spark_core.animation.vanilla.asAnimatable
import cn.solarmoon.spark_core.entity.attack.AttackSystem
import cn.solarmoon.spark_core.phys.attached_body.AnimatedCubeBody
import cn.solarmoon.spark_core.phys.attached_body.putBody
import cn.solarmoon.spark_core.skill.addSkillController
import cn.solarmoon.spirit_of_fight.feature.body.GuardAnimBody
import cn.solarmoon.spirit_of_fight.feature.body.SkillAttackAnimBody
import cn.solarmoon.spirit_of_fight.feature.fight_skill.controller.SwordFightSkillController
import cn.solarmoon.spirit_of_fight.fighter.EntityPatch
import cn.solarmoon.spirit_of_fight.fighter.getPatch
import net.minecraft.world.entity.player.Player


class PlayerPatch(
    val player: Player
): EntityPatch(player) {

    val level = player.level()
    val animatable = player.asAnimatable()
    val mainAttackSystem = AttackSystem(player)
    val offAttackSystem = AttackSystem(player)
    val attackBodyRight = SkillAttackAnimBody("attackRight", "rightItem", level, animatable, mainAttackSystem)
    val attackBodyLeft = SkillAttackAnimBody("attackLeft", "leftItem", level, animatable, offAttackSystem)
    val guardBodyRight = GuardAnimBody("guardRight", "rightItem", level, animatable)
    val guardBodyLeft = GuardAnimBody("guardLeft", "leftItem", level, animatable)
    val swordFightSkillController = SwordFightSkillController(player, player.asAnimatable())

    init {
        player.putBody(attackBodyRight)
        player.putBody(attackBodyLeft)
        player.putBody(guardBodyRight)
        player.putBody(guardBodyLeft)
        animatable.animData.model.bones.values.filter { it.name !in listOf("rightItem", "leftItem") }.forEach { bone ->
            val body = AnimatedCubeBody(bone.name, level, animatable)
            player.putBody(body)
        }

        weaponAttackBody = attackBodyRight
        weaponGuardBody = guardBodyRight

        player.addSkillController(swordFightSkillController)
    }

}