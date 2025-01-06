package cn.solarmoon.spirit_of_fight.feature.body

import cn.solarmoon.spark_core.animation.IEntityAnimatable
import cn.solarmoon.spark_core.phys.attached_body.AnimatedPivotBody
import net.minecraft.world.level.Level

class GuardAnimBody(
    bodyName: String,
    boneName: String,
    level: Level,
    animatable: IEntityAnimatable<*>
): AnimatedPivotBody(bodyName, boneName, level, animatable) {

    init {
        body.disable()
    }

}