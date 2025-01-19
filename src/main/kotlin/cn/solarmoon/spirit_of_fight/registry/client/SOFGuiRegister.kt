package cn.solarmoon.spirit_of_fight.registry.client

import cn.solarmoon.spirit_of_fight.SpiritOfFight
import cn.solarmoon.spirit_of_fight.feature.fight_skill.spirit.FightSpiritGui
import net.minecraft.resources.ResourceLocation
import net.neoforged.bus.api.IEventBus
import net.neoforged.neoforge.client.event.RegisterGuiLayersEvent
import net.neoforged.neoforge.client.gui.VanillaGuiLayers

object SOFGuiRegister {

    fun reg(event: RegisterGuiLayersEvent) {
        event.registerAbove(VanillaGuiLayers.CROSSHAIR, id("fight_spirit"), FightSpiritGui())
    }

    private fun id(name: String): ResourceLocation {
        return ResourceLocation.fromNamespaceAndPath(SpiritOfFight.MOD_ID, name)
    }

    @JvmStatic
    fun register(bus: IEventBus) {
        bus.addListener(::reg)
    }

}