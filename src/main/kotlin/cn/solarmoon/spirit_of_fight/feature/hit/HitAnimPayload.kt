package cn.solarmoon.spirit_of_fight.feature.hit

import cn.solarmoon.spark_core.SparkCore
import cn.solarmoon.spark_core.animation.IEntityAnimatable
import cn.solarmoon.spark_core.util.Side
import cn.solarmoon.spirit_of_fight.SpiritOfFight
import cn.solarmoon.spirit_of_fight.feature.fight_skill.spirit.getFightSpirit
import cn.solarmoon.spirit_of_fight.feature.fight_skill.sync.FightSpiritPayload
import cn.solarmoon.spirit_of_fight.feature.fight_skill.sync.FightSpiritPayload.Type
import cn.solarmoon.spirit_of_fight.registry.common.SOFRegistries
import net.minecraft.network.codec.ByteBufCodecs
import net.minecraft.network.codec.StreamCodec
import net.minecraft.network.protocol.common.custom.CustomPacketPayload
import net.minecraft.resources.ResourceLocation
import net.neoforged.neoforge.network.handling.IPayloadContext

class HitAnimPayload(
    val entityId: Int,
    val hitTypeId: Int,
    val strength: AttackStrength,
    val boneName: String,
    val posSide: Side,
    val side: Side
): CustomPacketPayload {

    override fun type(): CustomPacketPayload.Type<out CustomPacketPayload?> {
        return TYPE
    }

    companion object {
        @JvmStatic
        fun handleInClient(payload: HitAnimPayload, context: IPayloadContext) {
            val level = context.player().level()
            val entity = level.getEntity(payload.entityId) ?: return
            if (entity !is IEntityAnimatable<*>) return
            val hitType = SOFRegistries.HIT_TYPE.byId(payload.hitTypeId) ?: return
            val hitAnim = hitType.getHitAnimation(entity, payload.strength, payload.boneName, payload.posSide, payload.side) ?: return
            entity.animController.setAnimation(hitAnim, 0)
        }

        @JvmStatic
        val TYPE = CustomPacketPayload.Type<HitAnimPayload>(ResourceLocation.fromNamespaceAndPath(SpiritOfFight.MOD_ID, "hit_animation"))

        @JvmStatic
        val STREAM_CODEC = StreamCodec.composite(
            ByteBufCodecs.INT, HitAnimPayload::entityId,
            ByteBufCodecs.INT, HitAnimPayload::hitTypeId,
            AttackStrength.STREAM_CODEC, HitAnimPayload::strength,
            ByteBufCodecs.STRING_UTF8, HitAnimPayload::boneName,
            Side.STREAM_CODEC, HitAnimPayload::posSide,
            Side.STREAM_CODEC, HitAnimPayload::side,
            ::HitAnimPayload
        )
    }

}