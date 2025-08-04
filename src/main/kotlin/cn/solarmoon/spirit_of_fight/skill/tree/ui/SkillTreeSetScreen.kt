package cn.solarmoon.spirit_of_fight.skill.tree.ui

import cn.solarmoon.spirit_of_fight.SpiritOfFight
import cn.solarmoon.spirit_of_fight.skill.tree.SkillTreeSet
import net.minecraft.client.gui.GuiGraphics
import net.minecraft.client.gui.screens.Screen
import net.minecraft.network.chat.Component
import net.minecraft.resources.ResourceLocation
import kotlin.math.ceil

class SkillTreeSetScreen(
    val treeSet: SkillTreeSet
): Screen(Component.literal("技能树选择界面")) {

    // 资源路径
    val bambooScrollHead = ResourceLocation.fromNamespaceAndPath(SpiritOfFight.MOD_ID, "textures/gui/bamboo_scroll_head.png")
    val bambooScroll = ResourceLocation.fromNamespaceAndPath(SpiritOfFight.MOD_ID, "textures/gui/bamboo_scroll.png")
    val bambooScrollFoot = ResourceLocation.fromNamespaceAndPath(SpiritOfFight.MOD_ID, "textures/gui/bamboo_scroll_foot.png")

    // 尺寸常量（根据美术资源实际尺寸调整）
    private val buttonSize = 24      // 按钮实际像素尺寸
    private val scrollWidth = 30     // 竹简背景宽度
    private val scrollHeight = 166   // 竹简背景高度
    private val columnSpacing = 1   // 列间固定间距（竹简边缘间距）
    private val rowSpacing = 2      // 行间固定间距（按钮底部到下一个按钮顶部）
    private val scrollPaddingTop = 32// 竹简顶部留白
    private val columnWidth = columnSpacing + scrollWidth

    // 动画
    private var animationStartTime = 0L
    private val animationDuration = 500f // 总动画时长1秒
    val animProgress get() = ((System.currentTimeMillis() - animationStartTime) / animationDuration)

    lateinit var buttons: List<SkillTreeButton>

    override fun init() {
        super.init()
        val level = minecraft?.level ?: return

        animationStartTime = System.currentTimeMillis()
        // 创建按钮
        buttons = treeSet.map { tree ->
            val button = SkillTreeButton(
                tree,
                level,
                buttonSize
            ) { minecraft?.pushGuiLayer(SkillTreeScreen(tree, level)) }
            addRenderableWidget(button)
        }
    }

    override fun render(guiGraphics: GuiGraphics, mouseX: Int, mouseY: Int, partialTick: Float) {
        super.render(guiGraphics, mouseX, mouseY, partialTick)
    }

    override fun renderMenuBackground(guiGraphics: GuiGraphics) {
        super.renderMenuBackground(guiGraphics)
        renderBambooScroll(guiGraphics)
    }

    private fun renderBambooScroll(guiGraphics: GuiGraphics) {
        val totalItems = treeSet.size
        val columns = ceil(totalItems / 4.0).toInt() + 1

        // 计算总内容宽度
        val totalWidth = (columns - 1) * scrollWidth + (columns - 1) * columnSpacing
        val startX = (width - totalWidth) / 2
        val startY = (height - scrollHeight) / 2

        // 竹简头部 + 物品
        guiGraphics.blit(
            bambooScrollHead,
            startX - columnWidth, startY,
            0f, 0f,
            scrollWidth, scrollHeight,
            scrollWidth, scrollHeight
        )

        // 绘制每个竹简背景
        for (col in 0 until columns) {
            val colDelay = (columns - 1 - col) * 0.2f
            // 列间延迟
            val colProgress = (animProgress - colDelay).coerceIn(0f, 1f)

            val add = (col * columnWidth * colProgress - columnWidth * (1 - colProgress)).toInt()
            val x = startX + add
            val y = startY

            if (col != columns - 1) {
                guiGraphics.blit(
                    bambooScroll,
                    x, y,
                    0f, 0f,
                    scrollWidth, scrollHeight,
                    scrollWidth, scrollHeight
                )
            } else {
                guiGraphics.blit(
                    bambooScrollFoot,
                    x, y,
                    0f, 0f,
                    scrollWidth, scrollHeight,
                    scrollWidth, scrollHeight
                )
            }

            // 更新按钮位置和可见性
            for (row in 0 until 4) {
                val index = col * 4 + row
                if (index < buttons.size) {
                    val button = buttons[index]
                    val x = x + (scrollWidth - buttonSize) / 2
                    val y = y + scrollPaddingTop + (button.height + rowSpacing) * row
                    // 按钮位置跟随竹简
                    button.setPosition(x, y)
                    // 渐显效果（当进度超过50%时显示）
                    button.setAlpha((colProgress - 0.5f).coerceIn(0f, 1f) * 2)
                    button.visible = colProgress > 0.5f
                }
            }
        }
    }

}