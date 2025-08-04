package cn.solarmoon.spirit_of_fight.entity

import com.mojang.serialization.Codec
import net.minecraft.network.chat.Component
import net.minecraft.util.StringRepresentable
import net.minecraft.world.entity.Entity

enum class WieldStyle(
    private val id: String
): StringRepresentable {
    DEFAULT("default"), SPECIAL("special");

    val translatableName = Component.translatable("wield_style.${serializedName}")

    override fun getSerializedName(): String {
        return id
    }

    companion object {
        fun switch(entity: Entity) {
            when(entity.wieldStyle) {
                DEFAULT -> entity.wieldStyle = SPECIAL
                SPECIAL -> entity.wieldStyle = DEFAULT
            }
        }

        val CODEC: Codec<WieldStyle> = StringRepresentable.fromEnum(::values)
    }
}