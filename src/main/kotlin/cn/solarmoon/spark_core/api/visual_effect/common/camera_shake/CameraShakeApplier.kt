package cn.solarmoon.spark_core.api.visual_effect.common.camera_shake

import cn.solarmoon.spark_core.registry.common.SparkVisualEffects
import net.neoforged.bus.api.SubscribeEvent
import net.neoforged.neoforge.client.event.ViewportEvent

class CameraShakeApplier {

    @SubscribeEvent
    fun cameraSetup(event: ViewportEvent.ComputeCameraAngles) {
        SparkVisualEffects.CAMERA_SHAKE.setupCamera(event)
    }

}