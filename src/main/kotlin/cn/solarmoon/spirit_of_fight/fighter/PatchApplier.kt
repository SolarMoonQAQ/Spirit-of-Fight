package cn.solarmoon.spirit_of_fight.fighter

import net.minecraft.world.entity.player.Player
import net.neoforged.bus.api.EventPriority
import net.neoforged.bus.api.SubscribeEvent
import net.neoforged.neoforge.event.entity.EntityJoinLevelEvent
import net.neoforged.neoforge.event.tick.EntityTickEvent

object PatchApplier {

    @SubscribeEvent(priority = EventPriority.HIGHEST)
    private fun control(event: EntityTickEvent.Pre) {
        val entity = event.entity
        val patch = entity.getPatch()
        patch.isAttacking = false
        patch.moveInputFreeze = false
        patch.preInputFreeze = false
        patch.operateFreeze = false
    }

    @SubscribeEvent(priority = EventPriority.LOW)
    private fun join(event: EntityJoinLevelEvent) {
        val entity = event.entity
        val level = event.level
        entity.getPatch().onJoinLevel(level)
    }

}