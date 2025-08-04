package cn.solarmoon.spirit_of_fight.registry.common

import cn.solarmoon.spark_core.animation.anim.origin.AnimIndex
import cn.solarmoon.spark_core.registry.common.SparkRegistries
import cn.solarmoon.spark_core.resource.common.SparkResourcePathBuilder
import cn.solarmoon.spark_core.util.MultiModuleResourceExtractionUtil
import cn.solarmoon.spirit_of_fight.SpiritOfFight
import cn.solarmoon.spirit_of_fight.poise_system.EntityHitApplier
import cn.solarmoon.spirit_of_fight.poise_system.SOFHitTypes
import net.minecraft.resources.ResourceLocation

object SOFTypedAnimations {
    @JvmStatic
    fun register() {
    }

    @JvmStatic
    val HIT_ANIMS by lazy { createHitAnim() }

    @JvmStatic
    val PLAYER_HIT_LANDING by lazy {
        SpiritOfFight.REGISTER.typedAnimation()
            .id(MultiModuleResourceExtractionUtil.normalizeResourceName("hit_landing"))
            .animIndex(
                SparkRegistries.TYPED_ANIMATION.get(
                    SparkResourcePathBuilder.buildAnimationPath(
                        SpiritOfFight.MOD_ID,
                        SpiritOfFight.MOD_ID,
                        "player",
                        MultiModuleResourceExtractionUtil.normalizeResourceName("Hit/landing")
                    )
                )!!.index
            )
            .provider {
                EntityHitApplier.hitAnimDoFreeze(this)
            }
            .build()
    }

    @JvmStatic
    val PARRIED_LEFT by lazy {
        SpiritOfFight.REGISTER.typedAnimation()
            .id(MultiModuleResourceExtractionUtil.normalizeResourceName("parried_left"))
            .animIndex(
                SparkRegistries.TYPED_ANIMATION.get(
                    SparkResourcePathBuilder.buildAnimationPath(
                        SpiritOfFight.MOD_ID,
                        SpiritOfFight.MOD_ID,
                        "player",
                        MultiModuleResourceExtractionUtil.normalizeResourceName("common:parried_left")
                    )
                )!!.index
            )
            .provider {
                EntityHitApplier.hitAnimDoFreeze(this)
            }
            .build()
    }

    @JvmStatic
    val PARRIED_RIGHT by lazy {
        SpiritOfFight.REGISTER.typedAnimation()
            .id(MultiModuleResourceExtractionUtil.normalizeResourceName("parried_right"))
            .animIndex(
                SparkRegistries.TYPED_ANIMATION.get(
                    SparkResourcePathBuilder.buildAnimationPath(
                        SpiritOfFight.MOD_ID,
                        SpiritOfFight.MOD_ID,
                        "player",
                        MultiModuleResourceExtractionUtil.normalizeResourceName("common:parried_right")
                    )
                )!!.index
            )
            .provider {
                EntityHitApplier.hitAnimDoFreeze(this)
            }
            .build()
    }

    fun createHitAnim(
        index: ResourceLocation = SparkResourcePathBuilder.buildResourcePath(SpiritOfFight.MOD_ID, SpiritOfFight.MOD_ID, "models", "player")
    ) =
        SOFHitTypes.HIT_ANIM_NAMES.mapValues { (animName, _) -> run {
            SpiritOfFight.REGISTER.typedAnimation()
                .id("${SpiritOfFight.MOD_ID}/player/${MultiModuleResourceExtractionUtil.normalizeResourceName(animName)}")
                .animIndex(
                    // 找到原来的animIndex,重新构造有provider的TypedAnimation
                    SparkRegistries.TYPED_ANIMATION.get(
                        SparkResourcePathBuilder.buildAnimationPath(
                            SpiritOfFight.MOD_ID,
                            SpiritOfFight.MOD_ID,
                            "player",
                            MultiModuleResourceExtractionUtil.normalizeResourceName(animName)
                        )
                    )!!.index
                )
                .provider {
                    EntityHitApplier.hitAnimDoFreeze(this)
                }
                .registerDynamic()
        }
    }

}