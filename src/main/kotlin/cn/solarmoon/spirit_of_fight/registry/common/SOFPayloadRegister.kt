package cn.solarmoon.spirit_of_fight.registry.common

import cn.solarmoon.spirit_of_fight.particle.sync.QuadraticParticlePayload
import cn.solarmoon.spirit_of_fight.poise_system.PoiseResetPayload
import cn.solarmoon.spirit_of_fight.poise_system.PoiseSetPayload
import cn.solarmoon.spirit_of_fight.spirit.FightSpiritClearPayload
import cn.solarmoon.spirit_of_fight.spirit.FightSpiritPayload
import cn.solarmoon.spirit_of_fight.sync.MoveDirectionPayload
import cn.solarmoon.spirit_of_fight.sync.MovePayload
import net.neoforged.bus.api.IEventBus
import net.neoforged.neoforge.network.event.RegisterPayloadHandlersEvent
import net.neoforged.neoforge.network.handling.DirectionalPayloadHandler

object SOFPayloadRegister {

    private fun net(event: RegisterPayloadHandlersEvent) {
        val control = event.registrar("client_control")
        control.playBidirectional(MoveDirectionPayload.TYPE, MoveDirectionPayload.STREAM_CODEC, DirectionalPayloadHandler(MoveDirectionPayload::handle, MoveDirectionPayload::handle))
        control.playToClient(MovePayload.TYPE, MovePayload.STREAM_CODEC, MovePayload::handleInClient)
        control.playToServer(FightSpiritClearPayload.TYPE, FightSpiritClearPayload.STREAM_CODEC, FightSpiritClearPayload::handleInServer)
        val sync = event.registrar("sync")
        sync.playToClient(FightSpiritPayload.TYPE, FightSpiritPayload.STREAM_CODEC, FightSpiritPayload::handleInClient)
        sync.playToClient(PoiseSetPayload.TYPE, PoiseSetPayload.STREAM_CODEC, PoiseSetPayload::handleInClient)
        sync.playToClient(PoiseResetPayload.TYPE, PoiseResetPayload.STREAM_CODEC, PoiseResetPayload::handleInClient)
        val particle = event.registrar("particle")
        particle.playToClient(QuadraticParticlePayload.TYPE, QuadraticParticlePayload.STREAM_CODEC, QuadraticParticlePayload::handleInClient)
    }

    @JvmStatic
    fun register(modBus: IEventBus) {
        modBus.addListener(::net)
    }

}