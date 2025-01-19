package cn.solarmoon.spirit_of_fight.feature.fight_skill.spirit

import cn.solarmoon.spirit_of_fight.feature.fight_skill.sync.FightSpiritPayload
import com.mojang.serialization.Codec
import com.mojang.serialization.codecs.RecordCodecBuilder
import net.minecraft.util.Mth
import net.neoforged.neoforge.network.PacketDistributor

class FightSpirit {

    var value = 0
    var maxValue = 100
    var fadeTick = 0
    var maxTickToFade = 100
    var baseGrowth = 20

    var valueCache = 0

    val shouldFade get() = value > 0 && fadeTick >= maxTickToFade
    val isFull get() = value >= maxValue

    fun getProgress(partialTicks: Float = 1f): Float {
        return (Mth.lerp(partialTicks, valueCache.toFloat(), value.toFloat()) / maxValue).coerceIn(0f, 1f)
    }

    fun updateCache() {
        valueCache = value
    }

    fun addStage(amount: Int) {
        fadeTick = 0
        if (value < maxValue) {
            updateCache()
            value = (value + amount).coerceIn(0, maxValue)
        }
    }

    fun addStage(multiplier: Double) = addStage((baseGrowth * multiplier).toInt())

    fun clear() {
        valueCache = value
        value = 0
        fadeTick = 0
    }

    fun tick() {
        updateCache()
        if (value > 0) {
            if (shouldFade) {
                value--
            }
            else fadeTick++
        }
    }

    fun syncToClient(entityId: Int, operation: FightSpiritPayload.Type) {
        PacketDistributor.sendToAllPlayers(FightSpiritPayload(entityId, value, operation.id))
    }

    companion object {
        @JvmStatic
        val CODEC: Codec<FightSpirit> = RecordCodecBuilder.create {
            it.group(
                Codec.INT.fieldOf("value").forGetter { it.value },
                Codec.INT.fieldOf("max_value").forGetter { it.maxValue },
                Codec.INT.fieldOf("tick").forGetter { it.fadeTick },
                Codec.INT.fieldOf("max_tick").forGetter { it.maxTickToFade },
                Codec.INT.fieldOf("base_growth").forGetter { it.baseGrowth }
            ).apply(it) { a,b,c,d,e ->
                FightSpirit().apply { value = a; maxValue = b; fadeTick = c; maxTickToFade = d; baseGrowth = e }
            }
        }
    }

}