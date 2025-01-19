package cn.solarmoon.spirit_of_fight.registry.client

import cn.solarmoon.spark_core.event.ItemInHandModelRegisterEvent
import cn.solarmoon.spirit_of_fight.registry.common.SOFItems
import net.neoforged.bus.api.IEventBus

object SOFItemInHandModelRegister {

    private fun reg(event: ItemInHandModelRegisterEvent) {
        event.addInHandModel(SOFItems.IRON_HAMMER.get())
    }

    @JvmStatic
    fun register(bus: IEventBus) {
        bus.addListener(::reg)
    }

}