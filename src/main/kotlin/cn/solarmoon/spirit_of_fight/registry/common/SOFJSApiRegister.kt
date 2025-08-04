package cn.solarmoon.spirit_of_fight.registry.common

import cn.solarmoon.spark_core.event.SparkJSComponentRegisterEvent
import cn.solarmoon.spirit_of_fight.js.JSSOFHelper
import cn.solarmoon.spirit_of_fight.js.JSSOFParticlePresets
import net.neoforged.neoforge.common.NeoForge

object SOFJSApiRegister {

    private fun regCom(event: SparkJSComponentRegisterEvent) {
        event.registerComponent("SOFHelper", JSSOFHelper)
        event.registerComponent("SOFParticlePresets", JSSOFParticlePresets)
    }

    @JvmStatic
    fun register() {
        NeoForge.EVENT_BUS.addListener(::regCom)
    }

}