package cn.solarmoon.spirit_of_fight.feature.lock_on

import cn.solarmoon.spark_core.entity.smoothLookAt
import cn.solarmoon.spark_core.event.EntityTurnEvent
import cn.solarmoon.spirit_of_fight.registry.client.SOFKeyMappings
import net.minecraft.client.Minecraft
import net.minecraft.world.phys.EntityHitResult
import net.neoforged.bus.api.SubscribeEvent
import net.neoforged.neoforge.client.event.InputEvent
import net.neoforged.neoforge.client.event.ViewportEvent

class LockOnApplier {

    @SubscribeEvent
    private fun adjustTarget(event: InputEvent.Key) {
        val hit = Minecraft.getInstance().hitResult
        while (SOFKeyMappings.LOCK_ON.consumeClick()) {
            if (hit is EntityHitResult && !LockOnController.hasTarget) {
                LockOnController.setTarget(hit.entity)
            } else {
                LockOnController.setTarget(null)
            }
        }
    }

    @SubscribeEvent
    private fun lock(event: EntityTurnEvent) {
        val player = Minecraft.getInstance().player ?: return
        if (event.entity != player) return
        LockOnController.target?.let {
            event.isCanceled = true
        }
    }

    @SubscribeEvent
    private fun tick(event: ViewportEvent.ComputeCameraAngles) {
        val player = Minecraft.getInstance().player ?: return

        // 生物死亡后解绑
        if (LockOnController.target?.isRemoved == true) LockOnController.clear()

        LockOnController.target?.let {
            // 距离过远自动解绑
            if (player.distanceTo(it) > 64) LockOnController.clear()

            player.smoothLookAt(LockOnController.lookPos, event.partialTick.toFloat())
        }
    }

}