package cn.solarmoon.spirit_of_fight.registry.common

import cn.solarmoon.spirit_of_fight.SpiritOfFight
import cn.solarmoon.spirit_of_fight.feature.hit.HitType

object SOFRegistries {
    @JvmStatic
    fun register() {}

    @JvmStatic
    val HIT_TYPE = SpiritOfFight.REGISTER.registry<HitType>()
        .id("hit_type")
        .build { it.sync(true).create() }

}