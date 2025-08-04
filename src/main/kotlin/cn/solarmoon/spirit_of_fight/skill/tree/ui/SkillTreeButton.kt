package cn.solarmoon.spirit_of_fight.skill.tree.ui

import cn.solarmoon.spirit_of_fight.SpiritOfFight
import cn.solarmoon.spirit_of_fight.skill.tree.SkillTree
import com.mojang.blaze3d.systems.RenderSystem
import net.minecraft.client.gui.GuiGraphics
import net.minecraft.client.gui.components.ImageButton
import net.minecraft.client.gui.components.WidgetSprites
import net.minecraft.resources.ResourceLocation
import net.minecraft.world.level.Level

class SkillTreeButton(
    tree: SkillTree,
    level: Level,
    size: Int,
    onPress: OnPress,
): ImageButton(
    size,
    size,
    WidgetSprites(
        tree.getIcon(level.registryAccess()),
        ResourceLocation.fromNamespaceAndPath(SpiritOfFight.MOD_ID, "textures/gui/skill_tree_focused.png")
    ),
    onPress,
    tree.getName(level.registryAccess())
) {

    override fun renderWidget(guiGraphics: GuiGraphics, mouseX: Int, mouseY: Int, partialTick: Float) {
        RenderSystem.enableBlend()
        RenderSystem.defaultBlendFunc()
        val poseStack = guiGraphics.pose()
        val color = RenderSystem.getShaderColor()

        RenderSystem.setShaderColor(color[0], color[1], color[2], alpha)

        poseStack.pushPose()
        guiGraphics.blit(sprites.get(isActive, false), x, y, 0f, 0f, width, height, width, height)
        if (isHovered) {
            guiGraphics.blit(sprites.get(isActive, isHoveredOrFocused), x, y, 0f, 0f, width, height, width, height)
        }
        poseStack.popPose()

        RenderSystem.setShaderColor(color[0], color[1], color[2], color[3])

        RenderSystem.disableBlend()
    }

}