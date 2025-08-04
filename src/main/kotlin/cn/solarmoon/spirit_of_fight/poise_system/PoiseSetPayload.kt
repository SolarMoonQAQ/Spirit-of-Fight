package cn.solarmoon.spirit_of_fight.poise_system

import cn.solarmoon.spirit_of_fight.SpiritOfFight
import net.minecraft.network.codec.ByteBufCodecs
import net.minecraft.network.codec.StreamCodec
import net.minecraft.network.protocol.common.custom.CustomPacketPayload
import net.minecraft.resources.ResourceLocation
import net.neoforged.neoforge.network.handling.IPayloadContext

data class PoiseSetPayload(
    val entityId: Int,
    val poise: Int,
    val maxPoise: Int
): CustomPacketPayload {

    override fun type(): CustomPacketPayload.Type<out CustomPacketPayload?> {
        return TYPE
    }

    companion object {
        @JvmStatic
        fun handleInClient(payload: PoiseSetPayload, context: IPayloadContext) {
            val level = context.player().level()
            val entity = level.getEntity(payload.entityId) ?: return
            entity.poise.currentValue = payload.poise
            entity.poise.maxValue = payload.maxPoise
        }

        @JvmStatic
        val TYPE = CustomPacketPayload.Type<PoiseSetPayload>(ResourceLocation.fromNamespaceAndPath(SpiritOfFight.MOD_ID, "poise_set"))

        @JvmStatic
        val STREAM_CODEC = StreamCodec.composite(
            ByteBufCodecs.INT, PoiseSetPayload::entityId,
            ByteBufCodecs.INT, PoiseSetPayload::poise,
            ByteBufCodecs.INT, PoiseSetPayload::maxPoise,
            ::PoiseSetPayload
        )
    }

}