package cn.solarmoon.spirit_of_fight.data

import cn.solarmoon.spark_core.local_control.KeyEvent
import cn.solarmoon.spirit_of_fight.SpiritOfFight
import cn.solarmoon.spirit_of_fight.entity.WieldStyle
import cn.solarmoon.spirit_of_fight.registry.client.SOFKeyMappings
import cn.solarmoon.spirit_of_fight.registry.common.SOFPreInputs
import cn.solarmoon.spirit_of_fight.registry.common.SOFRegistries
import cn.solarmoon.spirit_of_fight.skill.tree.SkillTree
import cn.solarmoon.spirit_of_fight.skill.tree.condition.AnyCondition
import cn.solarmoon.spirit_of_fight.skill.tree.condition.FallDistanceCondition
import cn.solarmoon.spirit_of_fight.skill.tree.condition.FightSpiritCondition
import cn.solarmoon.spirit_of_fight.skill.tree.condition.HitTargetCondition
import cn.solarmoon.spirit_of_fight.skill.tree.condition.HoldCondition
import cn.solarmoon.spirit_of_fight.skill.tree.condition.JumpingCondition
import cn.solarmoon.spirit_of_fight.skill.tree.condition.KeyInputCondition
import cn.solarmoon.spirit_of_fight.skill.tree.condition.OffHandCondition
import cn.solarmoon.spirit_of_fight.skill.tree.condition.OnGroundCondition
import cn.solarmoon.spirit_of_fight.skill.tree.condition.ReverseCondition
import cn.solarmoon.spirit_of_fight.skill.tree.condition.SkillEndCondition
import cn.solarmoon.spirit_of_fight.skill.tree.condition.SprintingCondition
import cn.solarmoon.spirit_of_fight.skill.tree.node.CommonNode
import cn.solarmoon.spirit_of_fight.skill.tree.node.FightSpiritConsumeNode
import cn.solarmoon.spirit_of_fight.skill.tree.node.StopNode
import net.minecraft.core.RegistrySetBuilder
import net.minecraft.resources.ResourceKey
import net.minecraft.resources.ResourceLocation
import net.minecraft.tags.ItemTags
import net.minecraft.world.item.crafting.Ingredient
import net.neoforged.neoforge.common.Tags
import org.joml.Vector2i
import java.util.concurrent.ConcurrentLinkedQueue

class SOFSkillTrees(
    builder: RegistrySetBuilder,
) {

    companion object {
        val SWORD_SINGLE_WIELD_COMBO = sofKey("sword.single_wield.combo")
        val SWORD_DUAL_WIELD_COMBO = sofKey("sword.dual_wield.combo")
        val SWORD_JUMP_ATTACK = sofKey("sword_jump_attack")
        val SWORD_SPRINT_ATTACK = sofKey("sword_sprint_attack")
        val SWORD_GUARD = sofKey("sword_guard")
        val SWORD_DODGE = sofKey("sword_dodge")
        val SWORD_SWITCH_ATTACK = sofKey("sword.switch_attack")

        val GLOVES_COMBO = sofKey("gloves_combo")
        val GLOVES_JUMP_ATTACK = sofKey("gloves_jump_attack")
        val GLOVES_SPRINT_ATTACK = sofKey("gloves_sprint_attack")
        val GLOVES_GUARD = sofKey("gloves_guard")
        val GLOVES_DODGE = sofKey("gloves_dodge")
        val GLOVES_SKILL = sofKey("gloves_skill")

        val HAMMER_COMBO = sofKey("hammer_combo")
        val HAMMER_JUMP_ATTACK = sofKey("hammer_jump_attack")
        val HAMMER_SPRINT_ATTACK = sofKey("hammer_sprint_attack")
        val HAMMER_GUARD = sofKey("hammer_guard")
        val HAMMER_DODGE = sofKey("hammer_dodge")
        val HAMMER_SKILL = sofKey("hammer_skill")

        val AXE_COMBO = sofKey("axe_combo")
        val AXE_JUMP_ATTACK = sofKey("axe_jump_attack")
        val AXE_SPRINT_ATTACK = sofKey("axe_sprint_attack")
        val AXE_GUARD = sofKey("axe_guard")
        val AXE_DODGE = sofKey("axe_dodge")
        val AXE_SKILL = sofKey("axe_skill")

        val MACE_COMBO = sofKey("mace_combo")
        val MACE_JUMP_ATTACK = sofKey("mace_jump_attack")
        val MACE_SPRINT_ATTACK = sofKey("mace_sprint_attack")
        val MACE_GUARD = sofKey("mace_guard")
        val MACE_DODGE = sofKey("mace_dodge")
        val MACE_SKILL = sofKey("mace_skill")
        val MACE_FALL_ATTACK = sofKey("mace_fall_attack")

        fun sofKey(id: String) = ResourceKey.create(SOFRegistries.SKILL_TREE, ResourceLocation.fromNamespaceAndPath(SpiritOfFight.MOD_ID, id))
    }

    init {
        builder.add(SOFRegistries.SKILL_TREE) {
            it.register(SWORD_SINGLE_WIELD_COMBO,
                SkillTree(
                    Ingredient.of(ItemTags.SWORDS),
                    listOf(
                        CommonNode(
                            listOf(HoldCondition(WieldStyle.DEFAULT), KeyInputCondition(mapOf("key.attack" to KeyEvent.RELEASE), listOf(Vector2i(0, 3)))),
                            SOFSkillTypes.SWORD_SINGLE_WIELD_COMBO_1,
                            SOFPreInputs.ATTACK,
                            listOf(
                                CommonNode(
                                    listOf(HoldCondition(WieldStyle.DEFAULT), OffHandCondition(Ingredient.of(Tags.Items.TOOLS_SHIELD)), KeyInputCondition(mapOf(SOFKeyMappings.SPECIAL_ATTACK.name to KeyEvent.PULSE))),
                                    SOFSkillTypes.SHIELD_COMBO_C0,
                                    SOFPreInputs.ATTACK,
                                    children = listOf(
                                        CommonNode(
                                            listOf(HoldCondition(WieldStyle.DEFAULT), KeyInputCondition(mapOf("key.attack" to KeyEvent.RELEASE), listOf(Vector2i(0, 3))), HitTargetCondition()),
                                            SOFSkillTypes.SWORD_SHIELD_COMBO_C1,
                                            SOFPreInputs.ATTACK,
                                            children = listOf(
                                                CommonNode(
                                                    listOf(HoldCondition(WieldStyle.DEFAULT), KeyInputCondition(mapOf("key.attack" to KeyEvent.RELEASE), listOf(Vector2i(0, 3)))),
                                                    SOFSkillTypes.SWORD_SINGLE_WIELD_COMBO_3,
                                                    SOFPreInputs.ATTACK
                                                )
                                            ),
                                            10
                                        )
                                    ),
                                    10
                                ),
                                CommonNode(
                                    listOf(HoldCondition(WieldStyle.DEFAULT), KeyInputCondition(mapOf("key.attack" to KeyEvent.RELEASE), listOf(Vector2i(0, 3)))),
                                    SOFSkillTypes.SWORD_SINGLE_WIELD_COMBO_2,
                                    SOFPreInputs.ATTACK,
                                    listOf(
                                        CommonNode(
                                            listOf(HoldCondition(WieldStyle.DEFAULT), KeyInputCondition(mapOf("key.attack" to KeyEvent.RELEASE), listOf(Vector2i(0, 3)))),
                                            SOFSkillTypes.SWORD_SINGLE_WIELD_COMBO_3,
                                            SOFPreInputs.ATTACK
                                        )
                                    ),
                                    10
                                )
                            ),
                            10
                        )
                    )
                )
            )
            it.register(SWORD_DUAL_WIELD_COMBO,
                SkillTree(
                    Ingredient.of(ItemTags.SWORDS),
                    listOf(
                        CommonNode(
                            listOf(HoldCondition(WieldStyle.SPECIAL), KeyInputCondition(mapOf("key.attack" to KeyEvent.RELEASE), listOf(Vector2i(0, 3)))),
                            SOFSkillTypes.SWORD_DUAL_WIELD_COMBO_1,
                            SOFPreInputs.ATTACK,
                            listOf(
                                CommonNode(
                                    listOf(HoldCondition(WieldStyle.SPECIAL), KeyInputCondition(mapOf("key.attack" to KeyEvent.RELEASE), listOf(Vector2i(0, 3)))),
                                    SOFSkillTypes.SWORD_DUAL_WIELD_COMBO_2,
                                    SOFPreInputs.ATTACK,
                                    listOf(
                                        CommonNode(
                                            listOf(HoldCondition(WieldStyle.SPECIAL), KeyInputCondition(mapOf("key.attack" to KeyEvent.RELEASE), listOf(Vector2i(0, 3)))),
                                            SOFSkillTypes.SWORD_DUAL_WIELD_COMBO_3,
                                            SOFPreInputs.ATTACK
                                        )
                                    ),
                                    10
                                )
                            ),
                            10
                        )
                    )
                )
            )
            it.register(SWORD_JUMP_ATTACK,
                SkillTree(
                    Ingredient.of(ItemTags.SWORDS),
                    listOf(
                        CommonNode(
                            listOf(JumpingCondition(), KeyInputCondition(mapOf("key.attack" to KeyEvent.RELEASE))),
                            SOFSkillTypes.SWORD_JUMP_ATTACK,
                            SOFPreInputs.ATTACK
                        )
                    ),
                    2
                )
            )
            it.register(SWORD_SPRINT_ATTACK,
                SkillTree(
                    Ingredient.of(ItemTags.SWORDS),
                    listOf(
                        CommonNode(
                            listOf(SprintingCondition(), KeyInputCondition(mapOf("key.attack" to KeyEvent.RELEASE))),
                            SOFSkillTypes.SWORD_SPRINT_ATTACK,
                            SOFPreInputs.ATTACK
                        )
                    ),
                    1
                )
            )
            it.register(SWORD_GUARD,
                SkillTree(
                    Ingredient.of(ItemTags.SWORDS),
                    listOf(
                        CommonNode(
                            listOf(OffHandCondition(Ingredient.of(Tags.Items.TOOLS_SHIELD)), KeyInputCondition(mapOf(SOFKeyMappings.GUARD.name to KeyEvent.PRESS))),
                            SOFSkillTypes.SHIELD_GUARD,
                            SOFPreInputs.GUARD,
                            children = listOf(
                                StopNode(listOf(ReverseCondition(KeyInputCondition(mapOf(SOFKeyMappings.GUARD.name to KeyEvent.PRESS)))))
                            )
                        ),
                        CommonNode(
                            listOf(KeyInputCondition(mapOf(SOFKeyMappings.GUARD.name to KeyEvent.PRESS))),
                            SOFSkillTypes.SWORD_GUARD,
                            SOFPreInputs.GUARD,
                            children = listOf(
                                StopNode(listOf(ReverseCondition(KeyInputCondition(mapOf(SOFKeyMappings.GUARD.name to KeyEvent.PRESS)))))
                            )
                        )
                    )
                )
            )
            it.register(SWORD_DODGE,
                SkillTree(
                    Ingredient.of(ItemTags.SWORDS),
                    listOf(
                        CommonNode(
                            listOf(KeyInputCondition(mapOf(SOFKeyMappings.DODGE.name to KeyEvent.PULSE)), OnGroundCondition()),
                            SOFSkillTypes.SWORD_DODGE,
                            SOFPreInputs.DODGE
                        )
                    )
                )
            )
            it.register(SWORD_SWITCH_ATTACK,
                SkillTree(
                    Ingredient.of(ItemTags.SWORDS),
                    listOf(
                        CommonNode(
                            listOf(KeyInputCondition(mapOf(SOFKeyMappings.SPECIAL_ATTACK.name to KeyEvent.PRESS))),
                            SOFSkillTypes.SWORD_SWITCH_ATTACK,
                            SOFPreInputs.SPECIAL_ATTACK,
                        )
                    ),
                    3
                )
            )

            it.register(GLOVES_COMBO,
                SkillTree(
                    Ingredient.of(SOFItemTags.FORGE_GLOVES),
                    listOf(
                        CommonNode(
                            listOf(KeyInputCondition(mapOf("key.attack" to KeyEvent.RELEASE), listOf(Vector2i(0, 3)))),
                            SOFSkillTypes.GLOVES_COMBO_0,
                            SOFPreInputs.ATTACK,
                            listOf(
                                CommonNode(
                                    listOf(KeyInputCondition(mapOf("key.attack" to KeyEvent.RELEASE), listOf(Vector2i(0, 3)))),
                                    SOFSkillTypes.GLOVES_COMBO_1,
                                    SOFPreInputs.ATTACK,
                                    listOf(
                                        CommonNode(
                                            listOf(KeyInputCondition(mapOf("key.attack" to KeyEvent.RELEASE), listOf(Vector2i(0, 3)))),
                                            SOFSkillTypes.GLOVES_COMBO_2,
                                            SOFPreInputs.ATTACK
                                        )
                                    ),
                                    10
                                )
                            ),
                            10
                        )
                    )
                )
            )
            it.register(GLOVES_JUMP_ATTACK,
                SkillTree(
                    Ingredient.of(SOFItemTags.FORGE_GLOVES),
                    listOf(
                        CommonNode(
                            listOf(JumpingCondition(), KeyInputCondition(mapOf("key.attack" to KeyEvent.RELEASE))),
                            SOFSkillTypes.GLOVES_JUMP_ATTACK,
                            SOFPreInputs.ATTACK
                        )
                    ),
                    2
                )
            )
            it.register(GLOVES_SPRINT_ATTACK,
                SkillTree(
                    Ingredient.of(SOFItemTags.FORGE_GLOVES),
                    listOf(
                        CommonNode(
                            listOf(SprintingCondition(), KeyInputCondition(mapOf("key.attack" to KeyEvent.RELEASE))),
                            SOFSkillTypes.GLOVES_SPRINT_ATTACK,
                            SOFPreInputs.ATTACK
                        )
                    ),
                    1
                )
            )
            it.register(GLOVES_GUARD,
                SkillTree(
                    Ingredient.of(SOFItemTags.FORGE_GLOVES),
                    listOf(
                        CommonNode(
                            listOf(KeyInputCondition(mapOf(SOFKeyMappings.GUARD.name to KeyEvent.PRESS))),
                            SOFSkillTypes.GLOVES_GUARD,
                            SOFPreInputs.GUARD,
                            children = listOf(
                                StopNode(listOf(ReverseCondition(KeyInputCondition(mapOf(SOFKeyMappings.GUARD.name to KeyEvent.PRESS)))))
                            )
                        )
                    )
                )
            )
            it.register(GLOVES_DODGE,
                SkillTree(
                    Ingredient.of(SOFItemTags.FORGE_GLOVES),
                    listOf(
                        CommonNode(
                            listOf(KeyInputCondition(mapOf(SOFKeyMappings.DODGE.name to KeyEvent.PULSE)), OnGroundCondition()),
                            SOFSkillTypes.GLOVES_DODGE,
                            SOFPreInputs.DODGE
                        )
                    )
                )
            )
            it.register(GLOVES_SKILL,
                SkillTree(
                    Ingredient.of(SOFItemTags.FORGE_GLOVES),
                    listOf(
                        CommonNode(
                            listOf(KeyInputCondition(mapOf(SOFKeyMappings.SPECIAL_ATTACK.name to KeyEvent.PRESS))),
                            SOFSkillTypes.GLOVES_SKILL_0,
                            SOFPreInputs.SPECIAL_ATTACK,
                            children = listOf(
                                FightSpiritConsumeNode(
                                    listOf(
                                        AnyCondition(ConcurrentLinkedQueue(
                                            listOf(
                                                ReverseCondition(KeyInputCondition(mapOf(SOFKeyMappings.SPECIAL_ATTACK.name to KeyEvent.PRESS))),
                                                SkillEndCondition())
                                        )),
                                        FightSpiritCondition(100)
                                    ),
                                    SOFSkillTypes.GLOVES_SKILL_1,
                                    SOFPreInputs.SPECIAL_ATTACK
                                ),
                                StopNode(
                                    listOf(
                                        AnyCondition(ConcurrentLinkedQueue(
                                            listOf(
                                                ReverseCondition(KeyInputCondition(mapOf(SOFKeyMappings.SPECIAL_ATTACK.name to KeyEvent.PRESS))),
                                                SkillEndCondition())
                                        )),
                                    )
                                )
                            )
                        )
                    )
                )
            )

            it.register(HAMMER_COMBO,
                SkillTree(
                    Ingredient.of(SOFItemTags.FORGE_HAMMER),
                    listOf(
                        CommonNode(
                            listOf(KeyInputCondition(mapOf("key.attack" to KeyEvent.RELEASE), listOf(Vector2i(0, 3)))),
                            SOFSkillTypes.HAMMER_COMBO_0,
                            SOFPreInputs.ATTACK,
                            children = listOf(
                                CommonNode(
                                    listOf(KeyInputCondition(mapOf("key.attack" to KeyEvent.RELEASE), listOf(Vector2i(0, 3)))),
                                    SOFSkillTypes.HAMMER_COMBO_1,
                                    SOFPreInputs.ATTACK,
                                    children = listOf(
                                        CommonNode(
                                            listOf(KeyInputCondition(mapOf("key.attack" to KeyEvent.RELEASE), listOf(Vector2i(0, 3)))),
                                            SOFSkillTypes.HAMMER_COMBO_2,
                                            SOFPreInputs.ATTACK
                                        )
                                    ),
                                    10
                                ),
                                CommonNode(
                                    listOf(KeyInputCondition(mapOf(SOFKeyMappings.SPECIAL_ATTACK.name to KeyEvent.PULSE))),
                                    SOFSkillTypes.HAMMER_COMBO_C0,
                                    SOFPreInputs.ATTACK,
                                    children = listOf(
                                        CommonNode(
                                            listOf(KeyInputCondition(mapOf("key.attack" to KeyEvent.RELEASE), listOf(Vector2i(0, 3))), HitTargetCondition()),
                                            SOFSkillTypes.HAMMER_COMBO_C1,
                                            SOFPreInputs.ATTACK,
                                            children = listOf(
                                                CommonNode(
                                                    listOf(KeyInputCondition(mapOf("key.attack" to KeyEvent.RELEASE), listOf(Vector2i(0, 3)))),
                                                    SOFSkillTypes.HAMMER_COMBO_2,
                                                    SOFPreInputs.ATTACK
                                                )
                                            ),
                                            10
                                        )
                                    ),
                                    10
                                ),
                            ),
                            10
                        )
                    )
                )
            )
            it.register(HAMMER_JUMP_ATTACK,
                SkillTree(
                    Ingredient.of(SOFItemTags.FORGE_HAMMER),
                    listOf(
                        CommonNode(
                            listOf(JumpingCondition(), KeyInputCondition(mapOf("key.attack" to KeyEvent.RELEASE))),
                            SOFSkillTypes.HAMMER_JUMP_ATTACK,
                            SOFPreInputs.ATTACK
                        )
                    ),
                    2
                )
            )
            it.register(HAMMER_SPRINT_ATTACK,
                SkillTree(
                    Ingredient.of(SOFItemTags.FORGE_HAMMER),
                    listOf(
                        CommonNode(
                            listOf(SprintingCondition(), KeyInputCondition(mapOf("key.attack" to KeyEvent.RELEASE))),
                            SOFSkillTypes.HAMMER_SPRINT_ATTACK,
                            SOFPreInputs.ATTACK
                        )
                    ),
                    1
                )
            )
            it.register(HAMMER_GUARD,
                SkillTree(
                    Ingredient.of(SOFItemTags.FORGE_HAMMER),
                    listOf(
                        CommonNode(
                            listOf(KeyInputCondition(mapOf(SOFKeyMappings.GUARD.name to KeyEvent.PRESS))),
                            SOFSkillTypes.HAMMER_GUARD,
                            SOFPreInputs.GUARD,
                            children = listOf(
                                StopNode(listOf(ReverseCondition(KeyInputCondition(mapOf(SOFKeyMappings.GUARD.name to KeyEvent.PRESS)))))
                            )
                        )
                    )
                )
            )
            it.register(HAMMER_DODGE,
                SkillTree(
                    Ingredient.of(SOFItemTags.FORGE_HAMMER),
                    listOf(
                        CommonNode(
                            listOf(KeyInputCondition(mapOf(SOFKeyMappings.DODGE.name to KeyEvent.PULSE)), OnGroundCondition()),
                            SOFSkillTypes.HAMMER_DODGE,
                            SOFPreInputs.DODGE
                        )
                    )
                )
            )
            it.register(HAMMER_SKILL,
                SkillTree(
                    Ingredient.of(SOFItemTags.FORGE_HAMMER),
                    listOf(
                        FightSpiritConsumeNode(
                            listOf(KeyInputCondition(mapOf(SOFKeyMappings.SPECIAL_ATTACK.name to KeyEvent.PULSE)), FightSpiritCondition(100)),
                            SOFSkillTypes.HAMMER_SPECIAL_ATTACK,
                            SOFPreInputs.SPECIAL_ATTACK
                        )
                    ),
                    -1
                )
            )

            it.register(AXE_COMBO,
                SkillTree(
                    Ingredient.of(ItemTags.AXES),
                    listOf(
                        CommonNode(
                            listOf(KeyInputCondition(mapOf("key.attack" to KeyEvent.RELEASE), listOf(Vector2i(0, 3)))),
                            SOFSkillTypes.AXE_COMBO_0,
                            SOFPreInputs.ATTACK,
                            listOf(
                                CommonNode(
                                    listOf(OffHandCondition(Ingredient.of(Tags.Items.TOOLS_SHIELD)), KeyInputCondition(mapOf(SOFKeyMappings.SPECIAL_ATTACK.name to KeyEvent.PULSE))),
                                    SOFSkillTypes.SHIELD_COMBO_C0,
                                    SOFPreInputs.ATTACK,
                                    children = listOf(
                                        CommonNode(
                                            listOf(KeyInputCondition(mapOf("key.attack" to KeyEvent.RELEASE), listOf(Vector2i(0, 3))), HitTargetCondition()),
                                            SOFSkillTypes.AXE_SHIELD_COMBO_C1,
                                            SOFPreInputs.ATTACK,
                                            children = listOf(
                                                CommonNode(
                                                    listOf(KeyInputCondition(mapOf("key.attack" to KeyEvent.RELEASE), listOf(Vector2i(0, 3)))),
                                                    SOFSkillTypes.AXE_SHIELD_COMBO_C2,
                                                    SOFPreInputs.ATTACK
                                                )
                                            ),
                                            10
                                        )
                                    ),
                                    10
                                ),
                                CommonNode(
                                    listOf(KeyInputCondition(mapOf("key.attack" to KeyEvent.RELEASE), listOf(Vector2i(0, 3)))),
                                    SOFSkillTypes.AXE_COMBO_1,
                                    SOFPreInputs.ATTACK,
                                    listOf(
                                        CommonNode(
                                            listOf(KeyInputCondition(mapOf("key.attack" to KeyEvent.RELEASE), listOf(Vector2i(0, 3)))),
                                            SOFSkillTypes.AXE_COMBO_2,
                                            SOFPreInputs.ATTACK
                                        )
                                    ),
                                    10
                                )
                            ),
                            10
                        )
                    )
                )
            )
            it.register(AXE_JUMP_ATTACK,
                SkillTree(
                    Ingredient.of(ItemTags.AXES),
                    listOf(
                        CommonNode(
                            listOf(JumpingCondition(), KeyInputCondition(mapOf("key.attack" to KeyEvent.RELEASE))),
                            SOFSkillTypes.AXE_JUMP_ATTACK,
                            SOFPreInputs.ATTACK
                        )
                    ),
                    2
                )
            )
            it.register(AXE_SPRINT_ATTACK,
                SkillTree(
                    Ingredient.of(ItemTags.AXES),
                    listOf(
                        CommonNode(
                            listOf(SprintingCondition(), KeyInputCondition(mapOf("key.attack" to KeyEvent.RELEASE))),
                            SOFSkillTypes.AXE_SPRINT_ATTACK,
                            SOFPreInputs.ATTACK
                        )
                    ),
                    1
                )
            )
            it.register(AXE_GUARD,
                SkillTree(
                    Ingredient.of(ItemTags.AXES),
                    listOf(
                        CommonNode(
                            listOf(OffHandCondition(Ingredient.of(Tags.Items.TOOLS_SHIELD)), KeyInputCondition(mapOf(SOFKeyMappings.GUARD.name to KeyEvent.PRESS))),
                            SOFSkillTypes.SHIELD_GUARD,
                            SOFPreInputs.GUARD,
                            children = listOf(
                                StopNode(listOf(ReverseCondition(KeyInputCondition(mapOf(SOFKeyMappings.GUARD.name to KeyEvent.PRESS)))))
                            )
                        ),
                        CommonNode(
                            listOf(KeyInputCondition(mapOf(SOFKeyMappings.GUARD.name to KeyEvent.PRESS))),
                            SOFSkillTypes.AXE_GUARD,
                            SOFPreInputs.GUARD,
                            children = listOf(
                                StopNode(listOf(ReverseCondition(KeyInputCondition(mapOf(SOFKeyMappings.GUARD.name to KeyEvent.PRESS)))))
                            )
                        )
                    )
                )
            )
            it.register(AXE_DODGE,
                SkillTree(
                    Ingredient.of(ItemTags.AXES),
                    listOf(
                        CommonNode(
                            listOf(KeyInputCondition(mapOf(SOFKeyMappings.DODGE.name to KeyEvent.PULSE)), OnGroundCondition()),
                            SOFSkillTypes.AXE_DODGE,
                            SOFPreInputs.DODGE
                        )
                    )
                )
            )
            it.register(AXE_SKILL,
                SkillTree(
                    Ingredient.of(ItemTags.AXES),
                    listOf(
                        CommonNode(
                            listOf(KeyInputCondition(mapOf(SOFKeyMappings.SPECIAL_ATTACK.name to KeyEvent.PULSE))),
                            SOFSkillTypes.AXE_SKILL_GRAB,
                            SOFPreInputs.SPECIAL_ATTACK,
                            children = listOf(
                                CommonNode(
                                    listOf(HitTargetCondition()),
                                    SOFSkillTypes.AXE_SKILL_PULL,
                                    SOFPreInputs.SPECIAL_ATTACK,
                                    children = listOf(
                                        FightSpiritConsumeNode(
                                            listOf(FightSpiritCondition(100), KeyInputCondition(mapOf("key.attack" to KeyEvent.RELEASE))),
                                            SOFSkillTypes.AXE_SKILL_SPIN,
                                            SOFPreInputs.SPECIAL_ATTACK
                                        )
                                    )
                                )
                            ),
                            reserveTime = 5
                        )
                    ),
                    1
                )
            )

            it.register(MACE_COMBO,
                SkillTree(
                    Ingredient.of(Tags.Items.TOOLS_MACE),
                    listOf(
                        CommonNode(
                            listOf(KeyInputCondition(mapOf("key.attack" to KeyEvent.RELEASE), listOf(Vector2i(0, 3)))),
                            SOFSkillTypes.MACE_COMBO_0,
                            SOFPreInputs.ATTACK,
                            listOf(
                                CommonNode(
                                    listOf(OffHandCondition(Ingredient.of(Tags.Items.TOOLS_SHIELD)), KeyInputCondition(mapOf(SOFKeyMappings.SPECIAL_ATTACK.name to KeyEvent.PULSE))),
                                    SOFSkillTypes.SHIELD_COMBO_C0,
                                    SOFPreInputs.ATTACK,
                                    children = listOf(
                                        CommonNode(
                                            listOf(KeyInputCondition(mapOf("key.attack" to KeyEvent.RELEASE), listOf(Vector2i(0, 3))), HitTargetCondition()),
                                            SOFSkillTypes.MACE_SHIELD_COMBO_C1,
                                            SOFPreInputs.ATTACK,
                                            children = listOf(
                                                CommonNode(
                                                    listOf(KeyInputCondition(mapOf("key.attack" to KeyEvent.RELEASE), listOf(Vector2i(0, 3)))),
                                                    SOFSkillTypes.MACE_COMBO_2,
                                                    SOFPreInputs.ATTACK
                                                )
                                            ),
                                            10
                                        )
                                    ),
                                    10
                                ),
                                CommonNode(
                                    listOf(KeyInputCondition(mapOf("key.attack" to KeyEvent.RELEASE), listOf(Vector2i(0, 3)))),
                                    SOFSkillTypes.MACE_COMBO_1,
                                    SOFPreInputs.ATTACK,
                                    listOf(
                                        CommonNode(
                                            listOf(KeyInputCondition(mapOf("key.attack" to KeyEvent.RELEASE), listOf(Vector2i(0, 3)))),
                                            SOFSkillTypes.MACE_COMBO_2,
                                            SOFPreInputs.ATTACK
                                        )
                                    ),
                                    10
                                )
                            ),
                            10
                        )
                    )
                )
            )
            it.register(MACE_JUMP_ATTACK,
                SkillTree(
                    Ingredient.of(Tags.Items.TOOLS_MACE),
                    listOf(
                        CommonNode(
                            listOf(JumpingCondition(), KeyInputCondition(mapOf("key.attack" to KeyEvent.RELEASE))),
                            SOFSkillTypes.MACE_JUMP_ATTACK,
                            SOFPreInputs.ATTACK
                        )
                    ),
                    2
                )
            )
            it.register(MACE_SPRINT_ATTACK,
                SkillTree(
                    Ingredient.of(Tags.Items.TOOLS_MACE),
                    listOf(
                        CommonNode(
                            listOf(SprintingCondition(), KeyInputCondition(mapOf("key.attack" to KeyEvent.RELEASE))),
                            SOFSkillTypes.MACE_SPRINT_ATTACK,
                            SOFPreInputs.ATTACK
                        )
                    ),
                    1
                )
            )
            it.register(MACE_GUARD,
                SkillTree(
                    Ingredient.of(Tags.Items.TOOLS_MACE),
                    listOf(
                        CommonNode(
                            listOf(OffHandCondition(Ingredient.of(Tags.Items.TOOLS_SHIELD)), KeyInputCondition(mapOf(SOFKeyMappings.GUARD.name to KeyEvent.PRESS))),
                            SOFSkillTypes.SHIELD_GUARD,
                            SOFPreInputs.GUARD,
                            children = listOf(
                                StopNode(listOf(ReverseCondition(KeyInputCondition(mapOf(SOFKeyMappings.GUARD.name to KeyEvent.PRESS)))))
                            )
                        ),
                        CommonNode(
                            listOf(KeyInputCondition(mapOf(SOFKeyMappings.GUARD.name to KeyEvent.PRESS))),
                            SOFSkillTypes.MACE_GUARD,
                            SOFPreInputs.GUARD,
                            children = listOf(
                                StopNode(listOf(ReverseCondition(KeyInputCondition(mapOf(SOFKeyMappings.GUARD.name to KeyEvent.PRESS)))))
                            )
                        )
                    )
                )
            )
            it.register(MACE_DODGE,
                SkillTree(
                    Ingredient.of(Tags.Items.TOOLS_MACE),
                    listOf(
                        CommonNode(
                            listOf(KeyInputCondition(mapOf(SOFKeyMappings.DODGE.name to KeyEvent.PULSE)), OnGroundCondition()),
                            SOFSkillTypes.MACE_DODGE,
                            SOFPreInputs.DODGE
                        )
                    )
                )
            )
            it.register(MACE_FALL_ATTACK,
                SkillTree(
                    Ingredient.of(Tags.Items.TOOLS_MACE),
                    listOf(
                        CommonNode(
                            listOf(FallDistanceCondition(4f), KeyInputCondition(mapOf("key.attack" to KeyEvent.RELEASE))),
                            SOFSkillTypes.MACE_FALL_ATTACK,
                            SOFPreInputs.ATTACK
                        )
                    ),
                    2
                )
            )
            it.register(MACE_SKILL,
                SkillTree(
                    Ingredient.of(Tags.Items.TOOLS_MACE),
                    listOf(
                        CommonNode(
                            listOf(KeyInputCondition(mapOf(SOFKeyMappings.SPECIAL_ATTACK.name to KeyEvent.PRESS))),
                            SOFSkillTypes.MACE_SKILL_CHARGING,
                            SOFPreInputs.SPECIAL_ATTACK,
                            children = listOf(
                                FightSpiritConsumeNode(
                                    listOf(
                                        AnyCondition(ConcurrentLinkedQueue(
                                            listOf(
                                                ReverseCondition(KeyInputCondition(mapOf(SOFKeyMappings.SPECIAL_ATTACK.name to KeyEvent.PRESS))),
                                                SkillEndCondition()
                                            )
                                        )),
                                        FightSpiritCondition(100)
                                    ),
                                    SOFSkillTypes.MACE_SKILL,
                                    SOFPreInputs.SPECIAL_ATTACK
                                ),
                                StopNode(
                                    listOf(
                                        AnyCondition(ConcurrentLinkedQueue(
                                            listOf(
                                                ReverseCondition(KeyInputCondition(mapOf(SOFKeyMappings.SPECIAL_ATTACK.name to KeyEvent.PRESS))),
                                                SkillEndCondition()
                                            )
                                        ))
                                    )
                                )
                            )
                        )
                    )
                )
            )
        }
    }

}