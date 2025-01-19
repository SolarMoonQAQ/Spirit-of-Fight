package cn.solarmoon.spirit_of_fight.registry.common

import cn.solarmoon.spirit_of_fight.SpiritOfFight
import cn.solarmoon.spirit_of_fight.feature.hit.AttackStrength
import cn.solarmoon.spirit_of_fight.feature.hit.Chop
import cn.solarmoon.spirit_of_fight.feature.hit.Stab
import cn.solarmoon.spirit_of_fight.feature.hit.Swipe
import cn.solarmoon.spirit_of_fight.feature.hit.Upstroke
import net.neoforged.bus.api.IEventBus
import net.neoforged.neoforge.registries.DeferredRegister
import java.util.function.Supplier

object SOFHitTypes {

    val reg = DeferredRegister.create(SOFRegistries.HIT_TYPE, SpiritOfFight.MOD_ID)

    @JvmStatic
    fun register(bus: IEventBus) {
        reg.register(bus)
    }

    @JvmStatic
    val LIGHT_CHOP = reg.register("light_chop", Supplier { Chop(AttackStrength.LIGHT, false) })
    @JvmStatic
    val HEAVY_CHOP = reg.register("heavy_chop", Supplier { Chop(AttackStrength.HEAVY, false) })
    @JvmStatic
    val KNOCKDOWN_CHOP = reg.register("knockdown_chop", Supplier { Chop(AttackStrength.SUPER_HEAVY, true) })
    @JvmStatic
    val LIGHT_SWIPE = reg.register("light_swipe", Supplier { Swipe(AttackStrength.LIGHT, false) })
    @JvmStatic
    val HEAVY_SWIPE = reg.register("heavy_swipe", Supplier { Swipe(AttackStrength.HEAVY, false) })
    @JvmStatic
    val KNOCKDOWN_SWIPE = reg.register("knockdown_swipe", Supplier { Swipe(AttackStrength.SUPER_HEAVY, true) })
    @JvmStatic
    val LIGHT_STAB = reg.register("light_stab", Supplier { Stab(AttackStrength.LIGHT, false) })
    @JvmStatic
    val HEAVY_STAB = reg.register("heavy_stab", Supplier { Stab(AttackStrength.HEAVY, false) })
    @JvmStatic
    val KNOCKDOWN_STAB = reg.register("knockdown_stab", Supplier { Stab(AttackStrength.SUPER_HEAVY, true) })
    @JvmStatic
    val LIGHT_UPSTROKE = reg.register("light_upstroke", Supplier { Upstroke(AttackStrength.LIGHT, false) })
    @JvmStatic
    val HEAVY_UPSTROKE = reg.register("heavy_upstroke", Supplier { Upstroke(AttackStrength.HEAVY, false) })
    @JvmStatic
    val KNOCKDOWN_UPSTROKE = reg.register("knockdown_upstroke", Supplier { Upstroke(AttackStrength.SUPER_HEAVY, true) })


}