package cn.solarmoon.spirit_of_fight.registry.common

import cn.solarmoon.spirit_of_fight.SpiritOfFight

object SOFBodyTypes {
    @JvmStatic
    fun register() {}

    @JvmStatic
    val ATTACK = SpiritOfFight.REGISTER.bodyType()
        .id("attack")
        .build()

    @JvmStatic
    val GUARD = SpiritOfFight.REGISTER.bodyType()
        .id("guard")
        .build()

    @JvmStatic
    val PLAYER_BODY = SpiritOfFight.REGISTER.bodyType()
        .id("player_body")
        .build()

}