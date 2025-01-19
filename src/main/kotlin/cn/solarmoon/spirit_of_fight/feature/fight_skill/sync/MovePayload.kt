package cn.solarmoon.spirit_of_fight.feature.fight_skill.sync

import cn.solarmoon.spark_core.SparkCore
import cn.solarmoon.spark_core.data.SerializeHelper
import cn.solarmoon.spirit_of_fight.SpiritOfFight
import net.minecraft.network.codec.ByteBufCodecs
import net.minecraft.network.codec.StreamCodec
import net.minecraft.network.protocol.common.custom.CustomPacketPayload
import net.minecraft.resources.ResourceLocation
import net.minecraft.world.phys.Vec3
import net.neoforged.neoforge.network.PacketDistributor
import net.neoforged.neoforge.network.handling.IPayloadContext

data class MovePayload(
    val id: Int,
    val movement: Vec3
): CustomPacketPayload {

    override fun type(): CustomPacketPayload.Type<out CustomPacketPayload?> {
        return TYPE
    }

    companion object {
        @JvmStatic
        fun handleInClient(payload: MovePayload, context: IPayloadContext) {
            val level = context.player().level()
            level.getEntity(payload.id)?.deltaMovement = payload.movement
        }

        @JvmStatic
        fun moveEntityInClient(id: Int, movement: Vec3) {
            PacketDistributor.sendToAllPlayers(MovePayload(id, movement))
        }

        @JvmStatic
        val TYPE = CustomPacketPayload.Type<MovePayload>(ResourceLocation.fromNamespaceAndPath(SpiritOfFight.MOD_ID, "client_move"))

        @JvmStatic
        val STREAM_CODEC = StreamCodec.composite(
            ByteBufCodecs.INT, MovePayload::id,
            SerializeHelper.VEC3_STREAM_CODEC, MovePayload::movement,
            ::MovePayload
        )
    }

}