package cn.solarmoon.spirit_of_fight.skill.tree.ui

import cn.solarmoon.spark_core.util.ColorUtil
import cn.solarmoon.spirit_of_fight.SpiritOfFight
import cn.solarmoon.spirit_of_fight.skill.tree.SkillTree
import cn.solarmoon.spirit_of_fight.util.drawThickHLine
import com.mojang.blaze3d.systems.RenderSystem
import net.minecraft.client.Minecraft
import net.minecraft.client.gui.GuiGraphics
import net.minecraft.client.gui.screens.Screen
import net.minecraft.client.gui.screens.inventory.tooltip.TooltipRenderUtil
import net.minecraft.network.chat.Component
import net.minecraft.resources.ResourceLocation
import net.minecraft.world.level.Level
import java.awt.Color
import kotlin.math.max
import kotlin.math.min
import kotlin.ranges.contains

class SkillTreeScreen(
    val tree: SkillTree,
    val level: Level,
): Screen(Component.literal("技能树")) {

    lateinit var layout: SkillTreeLayout

    // 视图控制
    lateinit var viewController: ViewPortController

    // 技能描述框参数
    private var nodeToShow: SkillTreeNodeLayout? = null
    private var isTooltipShowing = false
    private var fadeTime = 0
    private val maxFadeTime = 5
    private val fadeProgress get() = (fadeTime.toFloat() + if (isTooltipShowing) partialTicks else -partialTicks).coerceIn(0f, maxFadeTime.toFloat()) / maxFadeTime
    private val partialTicks get() = Minecraft.getInstance().timer.getGameTimeDeltaPartialTick(false)

    override fun init() {
        super.init()
        viewController = ViewPortController(this)
        layout = SkillTreeLayout(tree, font, 1.0, 35, 30)
        addRenderableWidget(viewController)
        autoCenterView()
    }

    private fun autoCenterView() {
        val screenWidth = width.toFloat()
        val screenHeight = height.toFloat()

        // 计算需要的缩放比例
        val widthRatio = screenWidth / (layout.totalWidth * 1.2f)
        val heightRatio = screenHeight / (layout.totalHeight * 1.2f)
        viewController.currentScale = min(widthRatio, heightRatio).coerceIn(viewController.minScale, 0.45F)

        // 重置视图偏移
        viewController.viewOffset.set(0f, -layout.totalHeight * viewController.currentScale / 4)
    }

    override fun render(guiGraphics: GuiGraphics, mouseX: Int, mouseY: Int, partialTick: Float) {
        super.render(guiGraphics, mouseX, mouseY, partialTick)

        var isHovering = false
        val pose = guiGraphics.pose()
        pose.pushPose()
        viewController.applyTransform(guiGraphics.pose())
        layout.render(guiGraphics)
        layout.nodeLayouts.values.forEach {
            if (isHovering(it, mouseX, mouseY)) {
                isHovering = true
                onHovering(guiGraphics, it)
            }
        }
        isTooltipShowing = isHovering

        pose.popPose()
        renderHUD(guiGraphics)
    }

    override fun tick() {
        super.tick()

        if (isTooltipShowing) {
            if (fadeTime < maxFadeTime) fadeTime++
        } else {
            if (fadeTime > 0) fadeTime--
        }
    }

    private fun isHovering(layout: SkillTreeNodeLayout, mouseX: Int, mouseY: Int): Boolean {
        val transformedPos = viewController.screenToScene(mouseX, mouseY)
        val textureSize = layout.iconSize
        return transformedPos.x in ((layout.pos.first - textureSize).toDouble()..(layout.pos.first + textureSize).toDouble()) &&
                transformedPos.y in ((layout.pos.second - textureSize).toDouble()..(layout.pos.second + textureSize).toDouble())
    }

    private fun onHovering(guiGraphics: GuiGraphics, layout: SkillTreeNodeLayout) {
        // 选框
        guiGraphics.blit(
            ResourceLocation.fromNamespaceAndPath(SpiritOfFight.MOD_ID, "textures/gui/skill_tree_checkbox.png"),
            layout.pos.first - layout.iconSize / 2,
            layout.pos.second - layout.iconSize / 2,
            0f, 0f,
            layout.iconSize, layout.iconSize,
            layout.iconSize, layout.iconSize,
        )

        // 描述框
        nodeToShow = layout
    }

    private fun renderHUD(guiGraphics: GuiGraphics) {
        val poseStack = guiGraphics.pose()
        poseStack.pushPose()
        poseStack.translate(0f, 0f, 2000f)
        poseStack.scale(0.75f, 0.75f, 1f)
        val infoText = Component.translatable("gui.${SpiritOfFight.MOD_ID}.skill_tree.info", "%.02f".format(viewController.currentScale))
        guiGraphics.drawString(
            font,
            infoText,
            ((width - font.width(infoText) * 3 / 4) / 0.75 - 4).toInt(),
            ((height - font.lineHeight) / 0.75).toInt(),
            0xAAAAAA,
            true
        )
        poseStack.popPose()

        // 渲染固定位置的工具提示
        poseStack.pushPose()
        poseStack.translate(0f, 0f, 2000f)
        val scale = 0.75f
        poseStack.scale(scale, scale, 1f)
        // 绘制带透明度的Tooltip
        nodeToShow?.let { layout ->
            val node = layout.node
            val alpha = fadeProgress
            RenderSystem.setShaderColor(1.0f, 1.0f, 1.0f, alpha)
            val name = node.name
            val desc = font.split(node.description, max(128, font.width(name)))
            if (fadeTime <= 0) return@let
            // 计算背景大小
            val textWidth = max(font.width(name) + layout.iconSize / 2, desc.maxOf { font.width(it) })
            val bgWidth = textWidth + 8
            val bgHeight = font.lineHeight * (desc.size + 1) + layout.iconSize / 2 + 8

            // 绘制半透明背景
            TooltipRenderUtil.renderTooltipBackground(guiGraphics, 9, 9, bgWidth + 1, bgHeight + 1, 0)

            poseStack.pushPose()
            RenderSystem.enableBlend()
            RenderSystem.defaultBlendFunc()
            RenderSystem.disableDepthTest()
            guiGraphics.blit(
                node.icon,
                14, 14,
                0f, 0f,
                layout.iconSize / 2, layout.iconSize / 2,
                layout.iconSize / 2, layout.iconSize / 2
            )
            RenderSystem.disableBlend()

            guiGraphics.drawThickHLine(14, 14 + textWidth, 14 + layout.iconSize / 2 + 4, 1, ColorUtil.getColorAndSetAlpha(Color.WHITE.rgb, alpha))

            // 绘制名称
            guiGraphics.drawString(
                font,
                name,
                14 + layout.iconSize / 2 + 4,
                14 + layout.iconSize / 2 / 2 - font.lineHeight / 2,
                ColorUtil.getColorAndSetAlpha(Color.WHITE.rgb, alpha), // 应用透明度
                false
            )
            desc.forEachIndexed { index, text ->
                guiGraphics.drawString(
                    font,
                    text,
                    14,
                    14 + layout.iconSize / 2 + 4 + 5 + index * font.lineHeight,
                    ColorUtil.getColorAndSetAlpha(Color.WHITE.rgb, alpha), // 应用透明度
                    false
                )
            }
            RenderSystem.enableDepthTest()
            RenderSystem.setShaderColor(1.0f, 1.0f, 1.0f, 1.0f)
            poseStack.popPose()
        }
    }

}