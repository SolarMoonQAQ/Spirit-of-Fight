package cn.solarmoon.spirit_of_fight.registry.common

import cn.solarmoon.spark_core.animation.IEntityAnimatable
import cn.solarmoon.spark_core.animation.vanilla.asAnimatable
import cn.solarmoon.spark_core.event.SkillControllerRegisterEvent
import cn.solarmoon.spirit_of_fight.feature.fight_skill.controller.AxeFightSkillController
import cn.solarmoon.spirit_of_fight.feature.fight_skill.controller.HammerFightSkillController
import cn.solarmoon.spirit_of_fight.feature.fight_skill.controller.SwordFightSkillController
import net.minecraft.world.entity.player.Player
import net.neoforged.neoforge.common.NeoForge

object SOFSkillControllerRegister {

    private fun reg(event: SkillControllerRegisterEvent.Entity) {
        event.register(Player::class) { SwordFightSkillController(it, it.asAnimatable()) }
        event.register(Player::class) { HammerFightSkillController(it, it.asAnimatable()) }
        event.register(Player::class) { AxeFightSkillController(it, it.asAnimatable()) }
    }

    @JvmStatic
    fun register() {
        NeoForge.EVENT_BUS.addListener(::reg)
    }

}