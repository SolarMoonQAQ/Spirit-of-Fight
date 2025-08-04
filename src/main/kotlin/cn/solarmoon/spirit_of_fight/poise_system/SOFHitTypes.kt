package cn.solarmoon.spirit_of_fight.poise_system

import cn.solarmoon.spark_core.util.Side

enum class SOFHitTypes {
    LIGHT_CHOP, LIGHT_STAB, LIGHT_SWIPE, LIGHT_UPSTROKE,
    HEAVY_CHOP, HEAVY_STAB, HEAVY_SWIPE, HEAVY_UPSTROKE,
    KNOCKDOWN_CHOP, KNOCKDOWN_STAB, KNOCKDOWN_SWIPE, KNOCKDOWN_UPSTROKE;

    companion object {
        val HIT_ANIM_NAMES = buildMap {
            SOFHitTypes.entries.forEach { hitType ->
                setOf("head", "waist", "leftArm", "rightArm", "leftLeg", "rightLeg").forEach { boneName ->
                    Side.entries.forEach { posSide ->
                        listOf(Side.LEFT, Side.RIGHT).forEach { hitSide ->
                            val name = getHitAnimation(hitType.toString().lowercase(), boneName, posSide, hitSide)
                            put(name, name)
                        }
                    }
                }
            }
        }

        fun getHitAnimation(
            hitType: String,
            boneName: String,
            posSide: Side,
            hitSide: Side
        ): String {
            val suffix = when (posSide) {
                Side.FRONT -> when (boneName) {
                    "head" -> "$hitType:head_${hitSide.toString().lowercase()}"
                    "waist", "leftArm", "rightArm" -> "$hitType:body_${hitSide.toString().lowercase()}"
                    "leftLeg", "rightLeg" -> "$hitType:leg_${hitSide.toString().lowercase()}"
                    else -> null
                }
                else -> when (boneName) {
                    "head", "waist", "leftArm", "rightArm" -> {
                        // 分割hitType取前半
                        val baseType = hitType.split('_').first()
                        "${baseType}_all:upperbody_${posSide.toString().lowercase()}"
                    }
                    "leftLeg", "rightLeg" -> {
                        val baseType = hitType.split('_').first()
                        "${baseType}_all:lowerbody_${posSide.toString().lowercase()}"
                    }
                    else -> null
                }
            } ?: "$hitType:${boneName}_$posSide#${hitSide.toString().lowercase()}"
            return suffix.let { "Hit/$it" }
        }
    }

}