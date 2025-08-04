package cn.solarmoon.spirit_of_fight.data

import cn.solarmoon.spirit_of_fight.SpiritOfFight
import net.minecraft.resources.ResourceLocation

object SOFSkillTypes {

    val SHIELD_COMBO_C0 = sofKey("shield_combo_c0")
    val SHIELD_GUARD = sofKey("shield_guard")

    val SWORD_SINGLE_WIELD_COMBO_1 = sofKey("sword.single_wield.combo_1")
    val SWORD_SINGLE_WIELD_COMBO_2 = sofKey("sword.single_wield.combo_2")
    val SWORD_SINGLE_WIELD_COMBO_3 = sofKey("sword.single_wield.combo_3")
    val SWORD_DUAL_WIELD_COMBO_1 = sofKey("sword.dual_wield.combo_1")
    val SWORD_DUAL_WIELD_COMBO_2 = sofKey("sword.dual_wield.combo_2")
    val SWORD_DUAL_WIELD_COMBO_3 = sofKey("sword.dual_wield.combo_3")
    val SWORD_SHIELD_COMBO_C1 = sofKey("sword_shield_combo_c1")
    val SWORD_SPRINT_ATTACK = sofKey("sword_dash_attack")
    val SWORD_JUMP_ATTACK = sofKey("sword_jump_attack")
    val SWORD_DODGE = sofKey("sword.dodge")
    val SWORD_GUARD = sofKey("sword.guard")
    val SWORD_SWITCH_ATTACK = sofKey("sword.switch_attack")

    val HAMMER_COMBO_0 = sofKey("hammer_combo_0")
    val HAMMER_COMBO_1 = sofKey("hammer_combo_1")
    val HAMMER_COMBO_2 = sofKey("hammer_combo_2")
    val HAMMER_COMBO_C0 = sofKey("hammer_combo_c0")
    val HAMMER_COMBO_C1 = sofKey("hammer_combo_c1")
    val HAMMER_SPRINT_ATTACK = sofKey("hammer_dash_attack")
    val HAMMER_JUMP_ATTACK = sofKey("hammer_jump_attack")
    val HAMMER_DODGE = sofKey("hammer_dodge")
    val HAMMER_GUARD = sofKey("hammer_guard")
    val HAMMER_SPECIAL_ATTACK = sofKey("hammer_special_attack")

    val AXE_COMBO_0 = sofKey("axe_combo_0")
    val AXE_COMBO_1 = sofKey("axe_combo_1")
    val AXE_COMBO_2 = sofKey("axe_combo_2")
    val AXE_SHIELD_COMBO_C1 = sofKey("axe_shield_combo_c1")
    val AXE_SHIELD_COMBO_C2 = sofKey("axe_shield_combo_c2")
    val AXE_SPRINT_ATTACK = sofKey("axe_dash_attack")
    val AXE_JUMP_ATTACK = sofKey("axe_jump_attack")
    val AXE_DODGE = sofKey("axe_dodge")
    val AXE_GUARD = sofKey("axe_guard")
    val AXE_SKILL_GRAB = sofKey("axe_grab")
    val AXE_SKILL_PULL = sofKey("axe_pull")
    val AXE_SKILL_SPIN = sofKey("axe_spin")

    val GLOVES_COMBO_0 = sofKey("gloves_combo_0")
    val GLOVES_COMBO_1 = sofKey("gloves_combo_1")
    val GLOVES_COMBO_2 = sofKey("gloves_combo_2")
    val GLOVES_SPRINT_ATTACK = sofKey("gloves_dash_attack")
    val GLOVES_JUMP_ATTACK = sofKey("gloves_jump_attack")
    val GLOVES_DODGE = sofKey("gloves_dodge")
    val GLOVES_GUARD = sofKey("gloves_guard")
    val GLOVES_SKILL_0 = sofKey("gloves_skill_0")
    val GLOVES_SKILL_1 = sofKey("gloves_skill_1")

    val MACE_COMBO_0 = sofKey("mace_combo_0")
    val MACE_COMBO_1 = sofKey("mace_combo_1")
    val MACE_COMBO_2 = sofKey("mace_combo_2")
    val MACE_SHIELD_COMBO_C1 = sofKey("mace_shield_combo_c1")
    val MACE_SPRINT_ATTACK = sofKey("mace_dash_attack")
    val MACE_JUMP_ATTACK = sofKey("mace_jump_attack")
    val MACE_DODGE = sofKey("mace_dodge")
    val MACE_GUARD = sofKey("mace_guard")
    val MACE_SKILL_CHARGING = sofKey("mace_skill_charging")
    val MACE_SKILL = sofKey("mace_skill")
    val MACE_FALL_ATTACK = sofKey("mace_fall_attack")

    fun sofKey(id: String) = ResourceLocation.fromNamespaceAndPath(SpiritOfFight.MOD_ID, id)
}