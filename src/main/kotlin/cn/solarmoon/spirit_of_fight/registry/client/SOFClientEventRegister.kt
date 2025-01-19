package cn.solarmoon.spirit_of_fight.registry.client

import cn.solarmoon.spirit_of_fight.fighter.player.CameraAdjuster
import cn.solarmoon.spirit_of_fight.feature.lock_on.LockOnApplier
import cn.solarmoon.spirit_of_fight.fighter.player.PlayerLocalController
import net.neoforged.neoforge.common.NeoForge

object SOFClientEventRegister {

    @JvmStatic
    fun register() {
        add(CameraAdjuster())
        add(LockOnApplier())
        add(PlayerLocalController)
    }

    private fun add(event: Any) {
        NeoForge.EVENT_BUS.register(event)
    }

}