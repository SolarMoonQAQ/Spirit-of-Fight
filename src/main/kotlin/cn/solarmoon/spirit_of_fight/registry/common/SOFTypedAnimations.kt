package cn.solarmoon.spirit_of_fight.registry.common

import cn.solarmoon.spark_core.animation.anim.origin.AnimIndex
import cn.solarmoon.spark_core.animation.anim.play.TypedAnimation
import cn.solarmoon.spark_core.registry.common.SparkRegistries
import cn.solarmoon.spark_core.util.MultiModuleResourceExtractionUtil
import cn.solarmoon.spirit_of_fight.poise_system.EntityHitApplier
import cn.solarmoon.spirit_of_fight.poise_system.SOFHitTypes
import net.minecraft.resources.ResourceKey
import net.minecraft.resources.ResourceLocation

object SOFTypedAnimations {

    private const val MOD_ID = "spirit_of_fight"

    @JvmStatic
    fun register() {
        // 可选：如需预注册，在此触发一次 get() 填充注册表
        // PLAYER_HIT_LANDING.get()
        // HIT_ANIMS.values.forEach { it.get() }
    }

    // 轻量引用：按需从注册表读取，不存在则创建并注册
    class LazyTypedAnimationRef(
        private val entity: String,
        private val anim: String
    ) {
        fun get(): TypedAnimation? {
            val normalized = MultiModuleResourceExtractionUtil.normalizeResourceName(anim)
            val registry = SparkRegistries.TYPED_ANIMATION

            val keyPath = "$MOD_ID/animations/$entity/$normalized"
            val keyRl = ResourceLocation.fromNamespaceAndPath(MOD_ID, keyPath)
            val key = ResourceKey.create(registry.key(), keyRl)

            // 已存在则直接返回
            registry[key]?.let { return it }

            // 不存在则创建并注册
            val index = AnimIndex(
                inputPath = ResourceLocation.withDefaultNamespace(entity), // 如 minecraft:player
                name = normalized,
                useShortcutConversion = false
            )
            val ta = TypedAnimation(index) {
                EntityHitApplier.hitAnimDoFreeze(this)
            }

            registry.registerDynamic(
                name = keyRl,
                value = ta,
                moduleId = MOD_ID,
                replace = true,
                triggerCallback = true
            )
            return ta
        }
    }

    @JvmStatic
    val HIT_ANIMS: Map<String, LazyTypedAnimationRef> =
        SOFHitTypes.HIT_ANIM_NAMES.mapValues { (animName, _) ->
            LazyTypedAnimationRef("player", MultiModuleResourceExtractionUtil.normalizeResourceName(animName))
        }

    @JvmStatic
    val PLAYER_HIT_LANDING = LazyTypedAnimationRef("player", "hit.landing")
}