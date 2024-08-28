package cn.solarmoon.solarmoon_core.api.recipe

import com.google.gson.JsonElement
import com.google.gson.JsonObject
import com.google.gson.JsonParser
import com.google.gson.JsonSyntaxException
import com.mojang.brigadier.exceptions.CommandSyntaxException
import com.mojang.serialization.Codec
import com.mojang.serialization.codecs.RecordCodecBuilder
import net.minecraft.core.Holder
import net.minecraft.core.NonNullList
import net.minecraft.core.registries.BuiltInRegistries
import net.minecraft.core.registries.Registries
import net.minecraft.nbt.TagParser
import net.minecraft.network.FriendlyByteBuf
import net.minecraft.network.RegistryFriendlyByteBuf
import net.minecraft.network.codec.ByteBufCodecs
import net.minecraft.network.codec.StreamCodec
import net.minecraft.resources.ResourceLocation
import net.minecraft.util.GsonHelper
import net.minecraft.util.RandomSource
import net.minecraft.world.InteractionHand
import net.minecraft.world.effect.MobEffects
import net.minecraft.world.entity.player.Player
import net.minecraft.world.item.ItemStack
import net.minecraft.world.item.enchantment.EnchantmentHelper
import net.minecraft.world.item.enchantment.Enchantments
import java.util.*
import java.util.stream.Collectors


data class ChanceResult(private val stack: ItemStack, private val chance: Float) {

    companion object {
        @JvmStatic
        val EMPTY: ChanceResult = ChanceResult(ItemStack.EMPTY, 1f)

        @JvmStatic
        val CODEC: Codec<ChanceResult> = RecordCodecBuilder.create { instance ->
            instance.group(
                ItemStack.OPTIONAL_CODEC.fieldOf("itemstack").forGetter { it.stack },
                Codec.FLOAT.optionalFieldOf("chance", 1f).forGetter { it.chance }
            ).apply(instance, ::ChanceResult)
        }

        @JvmStatic
        val STREAM_CODEC: StreamCodec<RegistryFriendlyByteBuf, ChanceResult> = StreamCodec.composite(
            ItemStack.STREAM_CODEC, ChanceResult::stack,
            ByteBufCodecs.FLOAT, ChanceResult::chance,
            ::ChanceResult
        )
    }

    fun rollOutput(rand: RandomSource, fortuneLevel: Int): ItemStack {
        var outputAmount: Int = stack.count

        for (roll in 0 until stack.count) {
            if (rand.nextFloat().toDouble() > chance.toDouble() + fortuneLevel.toDouble() * 0.1) {
                --outputAmount
            }
        }

        if (outputAmount == 0) {
            return ItemStack.EMPTY
        } else {
            val out: ItemStack = stack.copy()
            out.count = outputAmount
            return out
        }
    }

    /**
     * @return 获取所有可能的输出物品
     */
    fun getResults(chanceResults: NonNullList<ChanceResult>): List<ItemStack> {
        return chanceResults.stream()
            .map(ChanceResult::stack)
            .collect(Collectors.toList())
    }

    /**
     * 根据幸运等级对results进行随机选取并输出最终结果
     */
    fun getRolledResults(player: Player, chanceResults: NonNullList<ChanceResult>): List<ItemStack> {
        val fortuneLevel = player.getItemInHand(InteractionHand.MAIN_HAND).getEnchantmentLevel(player.level().registryAccess().lookupOrThrow(Registries.ENCHANTMENT).getOrThrow(Enchantments.FORTUNE))
        val luckEffect = player.getEffect(MobEffects.LUCK)
        val luckPotionLevel = if ((luckEffect != null)) luckEffect.amplifier + 1 else 0
        val rand = player.random
        val luck = fortuneLevel + luckPotionLevel
        val results: MutableList<ItemStack> = ArrayList()
        for (output in chanceResults) {
            val stack = output.rollOutput(rand, luck)
            if (!stack.isEmpty) {
                results.add(stack)
            }
        }
        return results
    }

}