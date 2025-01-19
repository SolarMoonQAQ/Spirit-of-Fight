package cn.solarmoon.spirit_of_fight.feature.hit

import cn.solarmoon.spark_core.animation.IEntityAnimatable
import cn.solarmoon.spark_core.entity.attack.AttackedData
import cn.solarmoon.spirit_of_fight.registry.common.SOFRegistries

fun AttackedData.getHitType() = extraData.getInt("hitType").let { SOFRegistries.HIT_TYPE.byId(it) }

fun AttackedData.setHitType(hitType: HitType) {
    extraData.putInt("hitType", hitType.id)
}

