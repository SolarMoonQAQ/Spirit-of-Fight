package cn.solarmoon.spirit_of_fight.registry.common

import cn.solarmoon.spirit_of_fight.SpiritOfFight
import cn.solarmoon.spirit_of_fight.item.HammerItem
import net.minecraft.world.entity.ai.attributes.Attributes
import net.minecraft.world.item.DiggerItem
import net.minecraft.world.item.Item
import net.minecraft.world.item.Tier
import net.minecraft.world.item.Tiers
import net.minecraft.world.item.component.ItemAttributeModifiers

object SOFItems {
    @JvmStatic
    fun register() {}

    @JvmStatic
    val IRON_HAMMER = SpiritOfFight.REGISTER.item<HammerItem>()
        .id("iron_hammer")
        .bound { HammerItem(Tiers.IRON, Item.Properties().attributes(DiggerItem.createAttributes(Tiers.IRON, 6f, -3f)).durability(1024)) }
        .build()

}