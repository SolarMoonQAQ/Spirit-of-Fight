package cn.solarmoon.spark_core.api.phys.thread

import cn.solarmoon.spark_core.api.phys.obb.OrientedBoundingBox
import cn.solarmoon.spark_core.api.phys.ode.DAABB
import cn.solarmoon.spark_core.api.phys.ode.DAABBC
import cn.solarmoon.spark_core.api.phys.ode.DBox
import cn.solarmoon.spark_core.api.phys.ode.DGeom
import cn.solarmoon.spark_core.api.phys.ode.OdeHelper
import cn.solarmoon.spark_core.api.phys.ode.internal.DxBox
import cn.solarmoon.spark_core.api.phys.toDBox
import cn.solarmoon.spark_core.api.phys.toQuaternion
import cn.solarmoon.spark_core.api.phys.toVector3f
import cn.solarmoon.spark_core.registry.common.SparkVisualEffects
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.cancel
import kotlinx.coroutines.delay
import kotlinx.coroutines.isActive
import kotlinx.coroutines.launch
import net.minecraft.server.level.ServerLevel
import net.minecraft.world.level.Level
import net.neoforged.neoforge.common.NeoForge
import java.awt.Color

abstract class PhysLevel(
    open val level: Level
) {

    companion object {
        @JvmStatic
        val TICK_STEP = 20L
    }

    val thread = CoroutineScope(Dispatchers.Default)
    val world = OdeHelper.createWorld()
    val entitySpace = OdeHelper.createHashSpace()
    val contactGroup = OdeHelper.createJointGroup()

    init {
        world.setGravity(0.0, -9.81, 0.0) //设置重力
        world.setContactSurfaceLayer(0.01) //最大陷入深度，有助于防止抖振(虽然本来似乎也没)
        world.setERP(0.25)
        world.setCFM(0.00005)
        world.setAutoDisableFlag(true) //设置静止物体自动休眠以节约性能
        world.setAutoDisableSteps(5)
        world.setQuickStepNumIterations(40) //设定迭代次数以提高物理计算精度
        world.setQuickStepW(1.3)
        world.setContactMaxCorrectingVel(20.0)
        OdeHelper.createPlane(entitySpace, 0.0, 1.0, 0.0, -60.0)
    }

    fun load() {
        thread.launch {
            while (isActive) {
                val startTime = System.nanoTime()

                frequencyTick()

                val endTime = System.nanoTime()
                val executionTime = (endTime - startTime) / 1_000_000
                val remainingDelay = TICK_STEP - executionTime
                if (remainingDelay > 0) {
                    delay(remainingDelay)
                }
            }
        }
    }

    fun unLoad() {
        thread.cancel()
    }

    abstract fun frequencyTick()

}