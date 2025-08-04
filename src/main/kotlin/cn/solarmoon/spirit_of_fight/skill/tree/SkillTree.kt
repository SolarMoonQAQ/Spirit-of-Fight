package cn.solarmoon.spirit_of_fight.skill.tree

import cn.solarmoon.spark_core.preinput.PreInput
import cn.solarmoon.spark_core.skill.Skill
import cn.solarmoon.spirit_of_fight.SpiritOfFight
import cn.solarmoon.spirit_of_fight.registry.common.SOFRegistries
import cn.solarmoon.spirit_of_fight.skill.tree.node.SkillTreeNode
import com.mojang.serialization.Codec
import com.mojang.serialization.codecs.RecordCodecBuilder
import net.minecraft.client.player.Input
import net.minecraft.core.RegistryAccess
import net.minecraft.network.chat.Component
import net.minecraft.resources.ResourceLocation
import net.minecraft.world.entity.player.Player
import net.minecraft.world.item.crafting.Ingredient
import net.minecraft.world.level.Level

class SkillTree(
    val ingredient: Ingredient,
    val rootNodes: List<SkillTreeNode>,
    val priority: Int = 0
) {

    lateinit var root: SkillTreeSet

    private val path = ArrayDeque<Int>()

    var currentSkill: Skill? = null
    var currentNode: SkillTreeNode? = null
        private set
    var reserveTime = 0
        private set
    private var inputCooldown = 0

    fun tryAdvance(player: Player, input: Input, simulate: Boolean = false): Boolean {
        val cNode = currentNode
        val level = player.level()
        val preInput = player.preInput

        val ps = currentSkill
        if (ps != null && !ps.isActivated) {
            if (reserveTime > 0) reserveTime--
            else reset(player)
        }

        // 防连击缓冲
        if (inputCooldown > 0) {
            inputCooldown--
            return false
        }

        // 阶段1：首次触发根节点
        if (cNode == null) {
            rootNodes.forEachIndexed { index, rootNode ->
                if (rootNode.match(player, currentSkill)) {
                    if (!simulate) activateNode(rootNode, index, player, level, preInput, input, true)
                    return true
                }
            }
        }
        // 阶段2：触发子节点
        else {
            cNode.children.forEachIndexed { index, child ->
                if (child.match(player, currentSkill)) {
                    if (!simulate) activateNode(child, index, player, level, preInput, input, false)
                    return true
                }
            }
        }

        return false
    }

    private fun activateNode(
        node: SkillTreeNode,
        index: Int,
        player: Player,
        level: Level,
        preInput: PreInput,
        input: Input,
        root: Boolean
    ) {
        preInput.setInput(node.preInputId, node.preInputDuration) {
            val next = if (!root) currentNode?.nextNode(index) else rootNodes.getOrNull(index)
            if (next?.onEntry(player, level, this) == true) {
                inputCooldown = 1
                reserveTime = node.reserveTime

                path.add(index)
                currentNode = next
            }
        }
    }

    fun reset(player: Player) {
        path.clear()
        reserveTime = 0
        currentSkill = null
        currentNode = null
        player.chargingTime = 0
    }

    fun getNodeByPath(path: MutableList<Int>): SkillTreeNode? {
        if (path.isEmpty()) return null

        var result = rootNodes.getOrNull(path.removeFirst())
        while (path.isNotEmpty()) {
            result = result?.children?.getOrNull(path.removeFirst())
        }

        return result
    }

    fun getName(registryAccess: RegistryAccess): Component {
        val key = getRegistryKey(registryAccess)
        return Component.translatable("skill_tree.${key.namespace}.${key.path}")
    }

    fun getIcon(registryAccess: RegistryAccess): ResourceLocation {
        val key = getRegistryKey(registryAccess)
        return ResourceLocation.fromNamespaceAndPath(SpiritOfFight.MOD_ID, "textures/skill/tree/${key.path}.png")
    }

    fun getRegistryKey(registryAccess: RegistryAccess) = registryAccess.registry(SOFRegistries.SKILL_TREE).get().getKey(this) ?: throw NullPointerException("技能树尚未注册")

    companion object {
        val CODEC: Codec<SkillTree> = RecordCodecBuilder.create {
            it.group(
                Ingredient.CODEC.fieldOf("item").forGetter { it.ingredient },
                SkillTreeNode.CODEC.listOf().fieldOf("nodes").forGetter { it.rootNodes },
                Codec.INT.optionalFieldOf("priority", 0).forGetter { it.priority }
            ).apply(it, ::SkillTree)
        }
    }

}