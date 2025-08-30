package cn.solarmoon.spirit_of_fight.poise_system

import cn.solarmoon.spark_core.SparkCore
import cn.solarmoon.spark_core.animation.IEntityAnimatable
import cn.solarmoon.spark_core.animation.anim.play.AnimEvent
import cn.solarmoon.spark_core.animation.anim.play.AnimInstance
import cn.solarmoon.spark_core.animation.anim.play.layer.AnimLayerData
import cn.solarmoon.spark_core.animation.anim.play.layer.DefaultLayer
import cn.solarmoon.spark_core.camera.setCameraLock
import cn.solarmoon.spark_core.entity.attack.SparkHurtDatas
import cn.solarmoon.spark_core.entity.getLateralSide
import cn.solarmoon.spark_core.entity.getSide
import cn.solarmoon.spark_core.util.Key
import cn.solarmoon.spark_core.util.toVec3
import cn.solarmoon.spirit_of_fight.event.GetHitAnimationEvent
import cn.solarmoon.spirit_of_fight.js.JSSOFConfig
import cn.solarmoon.spirit_of_fight.registry.common.SOFPreInputs
import cn.solarmoon.spirit_of_fight.registry.common.SOFTypedAnimations
import com.jme3.math.Vector3f
import net.minecraft.core.registries.BuiltInRegistries
import net.minecraft.world.damagesource.DamageTypes
import net.minecraft.world.entity.Entity
import net.neoforged.bus.api.SubscribeEvent
import net.neoforged.neoforge.common.NeoForge
import net.neoforged.neoforge.event.entity.EntityJoinLevelEvent
import net.neoforged.neoforge.event.entity.living.LivingDamageEvent

object EntityHitApplier {

    val HIT_TYPE = Key.create<HitType>("hit_type")

    @SubscribeEvent
    private fun onHit(event: LivingDamageEvent.Post) {
        val victim = event.entity
        if (event.newDamage <= 0f) return
        if (victim !is IEntityAnimatable<*>) return
        val source = event.source
        val sourcePos = source.sourcePosition ?: return
        val attackData = source.extraData
        val collisionData = attackData.read(SparkHurtDatas.COLLISION) ?: return
        val hitType = attackData.read(HIT_TYPE) ?: return
        val strength = hitType.poiseDamage
        val poiseData = victim.poise

        if (poiseData.reduce(strength)) {
            val hitSide = victim.getLateralSide(collisionData.damagedBody.getPhysicsLocation(Vector3f()).toVec3())
            val posSide = victim.getSide(sourcePos)
            collisionData.damagedBody.let {
                val boneName = it.name
                val resultHitAnim = NeoForge.EVENT_BUS.post(GetHitAnimationEvent(victim, hitType, boneName, posSide, hitSide)).resultHitAnim
                resultHitAnim?.apply {
                    if (exist()) {
                        play(victim, DefaultLayer.MAIN_LAYER, AnimLayerData(enterTransitionTime = 0))
                        playToClient(victim, DefaultLayer.MAIN_LAYER, AnimLayerData(enterTransitionTime = 0))
                    } else SparkCore.LOGGER.warn("${victim.type} 缺少受击动画：${index}")
                }
                SparkCore.LOGGER.info("受击动画：${resultHitAnim?.index?.name}, ${hitType.name},${resultHitAnim?.index?.locationName},${resultHitAnim?.index?.inputPath},${resultHitAnim?.index?.index}")
            }
        }
    }

    @SubscribeEvent
    private fun getHitAnim(event: GetHitAnimationEvent) {
        val animName = SOFHitTypes.getHitAnimation(event.hitType.name, event.boneName, event.posSide, event.hitSide)
        val ref = SOFTypedAnimations.HIT_ANIMS[animName]
        if (ref != null) {
            val hasBones = event.animatable.model.bones.keys.containsAll(
                setOf("head", "waist", "leftArm", "rightArm", "leftLeg", "rightLeg")
            )
            if (hasBones) {
                event.resultHitAnim = ref.get()
            }
        }
    }

    @SubscribeEvent
    private fun entityInit(event: EntityJoinLevelEvent) {
        val entity = event.entity
        val key = BuiltInRegistries.ENTITY_TYPE.getKey(entity.type)
        JSSOFConfig.ENTITY_DEFAULT_POISE[key]?.let {
            entity.poise.initValue(it)
        }
    }

    fun hitAnimDoFreeze(anim: AnimInstance) {
        anim.apply {
            shouldTurnHead = false
            onEvent<AnimEvent.SwitchIn> {
                val entity = holder.animatable as? Entity ?: return@onEvent
                if (animIndex.index.path.contains("knockdown")) entity.isKnockedDown = true
                entity.isHitting = true
                entity.setCameraLock(true)
                entity.preInput.lock()
                entity.preInput.clear()
            }

            onEvent<AnimEvent.Tick> {
                val entity = holder.animatable as? Entity ?: return@onEvent
                if (time >= origin.animationLength / 2) {
                    entity.preInput.executeIfPresent(SOFPreInputs.DODGE)
                }
            }

            onEvent<AnimEvent.End> {
                val entity = holder.animatable as? Entity ?: return@onEvent
                entity.preInput.unlock()
                entity.isHitting = false
                entity.setCameraLock(false)
                if (animIndex.index.path.contains("knockdown")) entity.isKnockedDown = false
            }
        }
    }

    @SubscribeEvent
    private fun fall(event: LivingDamageEvent.Post) {
        val entity = event.entity
        if (entity.isKnockedDown) return
        if (entity !is IEntityAnimatable<*>) return
        if (event.source.typeHolder().`is`(DamageTypes.FALL) && event.newDamage > 0) {
            SOFTypedAnimations.PLAYER_HIT_LANDING.get()?.apply {
                // play(entity, DefaultLayer.MAIN_LAYER, AnimLayerData(transitionTime = 0))
                // playToClient(entity, DefaultLayer.MAIN_LAYER, AnimLayerData(transitionTime = 0))
            }
        }
    }
}