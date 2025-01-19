package cn.solarmoon.spirit_of_fight.feature.fight_skill.controller

/**
 * 接入此接口后，技能可在combo的选定节点快速变招到下一招
 */
interface IFSSwitchNextCombo {

    val switchComboIndex: Int

    val inputWindow: IntRange get() = 1..3

}