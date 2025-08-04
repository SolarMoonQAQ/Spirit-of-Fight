package cn.solarmoon.spirit_of_fight.poise_system

import cn.solarmoon.spirit_of_fight.SpiritOfFight
import net.minecraft.network.codec.ByteBufCodecs
import net.minecraft.network.codec.StreamCodec
import net.minecraft.network.protocol.common.custom.CustomPacketPayload
import net.minecraft.resources.ResourceLocation
import net.neoforged.neoforge.network.handling.IPayloadContext

data class PoiseResetPayload(
    val entityId: Int
): CustomPacketPayload {

    override fun type(): CustomPacketPayload.Type<out CustomPacketPayload?> {
        return TYPE
    }

    companion object {
        @JvmStatic
        fun handleInClient(payload: PoiseResetPayload, context: IPayloadContext) {
            val level = context.player().level()
            val entity = level.getEntity(payload.entityId) ?: return
            entity.poise.reset()
        }

        @JvmStatic
        val TYPE = CustomPacketPayload.Type<PoiseResetPayload>(ResourceLocation.fromNamespaceAndPath(SpiritOfFight.MOD_ID, "poise_reset"))

        @JvmStatic
        val STREAM_CODEC = StreamCodec.composite(
            ByteBufCodecs.INT, PoiseResetPayload::entityId,
            ::PoiseResetPayload
        )
    }

}