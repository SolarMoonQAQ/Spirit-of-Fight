package cn.solarmoon.spirit_of_fight.entity

import cn.solarmoon.spark_core.event.ChangePresetAnimEvent
import net.neoforged.bus.api.SubscribeEvent

object StateAnimApplier {

    @SubscribeEvent
    private fun transModifier(event: ChangePresetAnimEvent.PlayerState) {
//        val player = event.player
//        if (player.mainHandItem.`is`(SOFItemTags.FORGE_HAMMER)) {
//
//            when(event.state) {
//                is EntityStates.Idle -> event.newAnim = SOFTypedAnimations.HAMMER_IDLE.get()
//                is EntityStates.Walk -> event.newAnim = SOFTypedAnimations.HAMMER_WALK.get()
//                is EntityStates.WalkBack -> event.newAnim = SOFTypedAnimations.HAMMER_WALK_BACK.get()
//                is EntityStates.Sprinting -> event.newAnim = SOFTypedAnimations.HAMMER_SPRINTING.get()
//                is EntityStates.Fall -> event.newAnim = SOFTypedAnimations.HAMMER_FALL.get()
//            }
//        } else if (player.mainHandItem.`is`(SOFItemTags.FORGE_GLOVES)) {
//            when(event.state) {
//                is EntityStates.Idle -> event.newAnim = SOFTypedAnimations.BAIMEI_IDLE.get()
//                is EntityStates.Walk -> event.newAnim = SOFTypedAnimations.BAIMEI_WALK.get()
//                is EntityStates.WalkBack -> event.newAnim = SOFTypedAnimations.BAIMEI_WALK_BACK.get()
//                is EntityStates.Sprinting -> event.newAnim = SOFTypedAnimations.BAIMEI_SPRINTING.get()
//            }
//        } else if (player.offhandItem.`is`(Tags.Items.TOOLS_SHIELD)) {
//            when(event.state) {
//                is EntityStates.Sprinting -> event.newAnim = SOFTypedAnimations.SHIELD_SPRINTING.get()
//            }
//        }
    }

}