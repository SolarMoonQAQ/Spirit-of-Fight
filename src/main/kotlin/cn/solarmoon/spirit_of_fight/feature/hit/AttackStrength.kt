package cn.solarmoon.spirit_of_fight.feature.hit

import net.minecraft.network.codec.ByteBufCodecs

enum class AttackStrength(val value: Int) {
    LIGHT(1), HEAVY(2), SUPER_HEAVY(3);

    companion object {
        @JvmStatic
        val STREAM_CODEC = ByteBufCodecs.INT.map(
            { AttackStrength.entries[it] },
            { it.ordinal }
        )
    }
}