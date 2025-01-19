package cn.solarmoon.spirit_of_fight.feature.hit

import cn.solarmoon.spark_core.animation.IEntityAnimatable
import cn.solarmoon.spark_core.entity.attack.getExtraData
import cn.solarmoon.spark_core.entity.getLateralSide
import cn.solarmoon.spark_core.entity.getSide
import cn.solarmoon.spark_core.phys.toVec3
import cn.solarmoon.spirit_of_fight.fighter.getPatch
import cn.solarmoon.spirit_of_fight.registry.common.SOFTypedAnimations
import net.neoforged.bus.api.SubscribeEvent
import net.neoforged.neoforge.event.entity.living.LivingDamageEvent
import net.neoforged.neoforge.event.entity.living.LivingFallEvent
import net.neoforged.neoforge.network.PacketDistributor

object HitAnimationApplier {

    @SubscribeEvent
    private fun onHit(event: LivingDamageEvent.Post) {
        val victim = event.entity
        if (event.newDamage <= 0f) return
        if (victim !is IEntityAnimatable<*>) return
        val source = event.source
        val sourcePos = source.sourcePosition ?: return
        val attackData = source.getExtraData() ?: return
        val hitType = attackData.getHitType() ?: return
        val strength = hitType.strength
        val side = victim.getLateralSide(attackData.damageBox.position.toVec3())
        val posSide = victim.getSide(sourcePos)
        attackData.damagedBody?.let {
            val boneName = it.name
            val hitAnimation = hitType.getHitAnimation(victim, strength, boneName, posSide, side) ?: return
            victim.animController.setAnimation(hitAnimation, 0)
            PacketDistributor.sendToAllPlayers(HitAnimPayload(victim.id, hitType.id, strength, boneName, posSide, side))
        }
    }

    @SubscribeEvent
    private fun fall(event: LivingFallEvent) {
        val entity = event.entity
        if (entity !is IEntityAnimatable<*>) return
        if (event.distance > 4) {
            entity.animController.setAnimation("Hit/landing", 0) {
                onTick {
                    entity.getPatch().moveInputFreeze = true
                    entity.getPatch().preInputFreeze = true
                    entity.getPatch().operateFreeze = true
                }
            }
        }
    }

}