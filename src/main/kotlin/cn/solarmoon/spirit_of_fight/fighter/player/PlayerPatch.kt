package cn.solarmoon.spirit_of_fight.fighter.player

import cn.solarmoon.spark_core.animation.vanilla.asAnimatable
import cn.solarmoon.spark_core.entity.attack.AttackSystem
import cn.solarmoon.spark_core.phys.createAnimatedCubeBody
import cn.solarmoon.spirit_of_fight.feature.body.createGuardAnimBody
import cn.solarmoon.spirit_of_fight.feature.body.createSkillAttackAnimBody
import cn.solarmoon.spirit_of_fight.fighter.EntityPatch
import cn.solarmoon.spirit_of_fight.registry.common.SOFBodyTypes
import net.minecraft.world.entity.player.Player
import net.minecraft.world.level.Level


class PlayerPatch(
    val player: Player
): EntityPatch(player) {

    val level = player.level()
    val animatable = player.asAnimatable()
    val mainAttackSystem = AttackSystem(player)
    val offAttackSystem = AttackSystem(player)
    val attackBodyRight = createSkillAttackAnimBody("rightItem", SOFBodyTypes.ATTACK.get(), animatable, level, mainAttackSystem)
    val attackBodyLeft = createSkillAttackAnimBody("leftItem", SOFBodyTypes.ATTACK.get(), animatable, level, mainAttackSystem)
    val guardBody = createGuardAnimBody("rightItem", SOFBodyTypes.GUARD.get(), animatable, level)

    init {
        weaponAttackBody = attackBodyRight
        weaponGuardBody = guardBody
    }

    override fun onJoinLevel(level: Level) {
        super.onJoinLevel(level)

        animatable.model.bones.values.filter { it.name !in listOf("rightItem", "leftItem") }.forEach { bone ->
            createAnimatedCubeBody(bone.name, SOFBodyTypes.PLAYER_BODY.get(), animatable, level)
        }
    }

}