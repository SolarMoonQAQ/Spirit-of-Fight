package cn.solarmoon.spirit_of_fight.skill.tree.condition

import cn.solarmoon.spark_core.skill.Skill
import cn.solarmoon.spirit_of_fight.entity.WieldStyle
import com.mojang.serialization.MapCodec
import com.mojang.serialization.codecs.RecordCodecBuilder
import net.minecraft.network.chat.Component
import net.minecraft.world.entity.player.Player

class HoldCondition(
    val wieldStyle: WieldStyle
): SkillTreeCondition {

    override fun test(
        player: Player,
        skill: Skill?
    ): Boolean {
        return player.wieldStyle == wieldStyle
    }

    override val description: Component
        get() = Component.translatable("skill_tree_condition.${registryKey.namespace}.${registryKey.path}", wieldStyle.translatableName)

    override val codec: MapCodec<out SkillTreeCondition> = CODEC

    companion object {
        val CODEC: MapCodec<HoldCondition> = RecordCodecBuilder.mapCodec {
            it.group(
                WieldStyle.CODEC.fieldOf("hold").forGetter { it.wieldStyle }
            ).apply(it, ::HoldCondition)
        }
    }

}