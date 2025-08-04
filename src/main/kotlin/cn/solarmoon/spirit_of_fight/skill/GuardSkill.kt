package cn.solarmoon.spirit_of_fight.skill

import cn.solarmoon.spark_core.animation.IEntityAnimatable
import cn.solarmoon.spark_core.animation.anim.play.AnimEvent
import cn.solarmoon.spark_core.animation.anim.play.AnimInstance
import cn.solarmoon.spark_core.entity.addRelativeMovement
import cn.solarmoon.spark_core.entity.getLateralSide
import cn.solarmoon.spark_core.skill.Skill
import cn.solarmoon.spark_core.skill.SkillEvent
import cn.solarmoon.spark_core.skill.SkillPhase
import cn.solarmoon.spark_core.skill.payload.SkillPayload
import cn.solarmoon.spark_core.util.Side
import cn.solarmoon.spirit_of_fight.poise_system.EntityHitApplier
import cn.solarmoon.spirit_of_fight.registry.common.SOFTypedAnimations
import cn.solarmoon.spirit_of_fight.util.SkillHelper
import com.jme3.bullet.collision.PhysicsCollisionObject
import net.minecraft.nbt.CompoundTag
import net.minecraft.world.phys.Vec3
import net.neoforged.neoforge.event.entity.living.LivingIncomingDamageEvent
import net.neoforged.neoforge.network.PacketDistributor

class GuardSkill: Skill() {
    class GuardHurt(val event: LivingIncomingDamageEvent, val hitPos: Vec3): SkillEvent()
    class Parry(val event: LivingIncomingDamageEvent, val hitPos: Vec3): SkillEvent()

    lateinit var guardAnim: AnimInstance
    lateinit var hurtAnim: AnimInstance
    var parryAnim: AnimInstance? = null
    lateinit var guardBody: PhysicsCollisionObject

    init {
        init {
            val animatable = holder as? IEntityAnimatable<*> ?: return@init
            val entity = animatable.animatable
            val animName = config["anim_name"] as String
            val enableParry = config["enable_parry"] as Boolean
            guardAnim = AnimInstance.create(animatable, animName)
            hurtAnim = AnimInstance.create(animatable, animName + "_hurt") {
                EntityHitApplier.hitAnimDoFreeze(this)
            }
            config.put("guard_anim", guardAnim)
            config.put("hurt_anim", hurtAnim)
            if (enableParry) {
                parryAnim = AnimInstance.create(animatable, animName + "_parry")
                config.put("parry_anim", parryAnim!!)
            }

            onEvent<SkillEvent.ActiveStart> {
                guardBody = config["guard_body"] as PhysicsCollisionObject
                animatable.animController.setAnimation(guardAnim, 5)
            }

            hurtAnim.onEvent<AnimEvent.Completed> {
                animatable.animController.setAnimation(guardAnim, 0)
            }

            parryAnim?.onEvent<AnimEvent.Completed> {
                if (isActivated) animatable.animController.setAnimation(guardAnim, 0)
            }

            onEvent<SkillEvent.Hurt> {
                val event = it.event
                val sourcePos = event.source.sourcePosition ?: return@onEvent
                SkillHelper.guard(it.event.source, guardBody) { body, hitPos ->
                    it.event.isCanceled = true
                    val attacker = event.source.entity
                    if (enableParry && config["can_parry"] as? Boolean == true) {
                        if (attacker is IEntityAnimatable<*>) {
                            val side = entity.getLateralSide(sourcePos)
                            when(side) {
                                Side.LEFT -> {
                                    SOFTypedAnimations.PARRIED_RIGHT.get().apply {
                                        play(attacker, 0)
                                        playToClient(attacker.id, 0)
                                    }
                                }
                                Side.RIGHT -> {
                                    SOFTypedAnimations.PARRIED_LEFT.get().apply {
                                        play(attacker, 0)
                                        playToClient(attacker.id, 0)
                                    }
                                }
                                else -> {}
                            }
                        }
                        animatable.animController.setAnimation(parryAnim, 0)
                        PacketDistributor.sendToAllPlayers(SkillPayload(this, CompoundTag().apply {
                            putBoolean("parry", true)
                        }))
                        triggerEvent(Parry(event, hitPos))
                    } else if (hurtAnim.isCancelled) {
                        hurtAnim.refresh()
                        animatable.animController.setAnimation(hurtAnim, 0)
                        entity.addRelativeMovement(sourcePos, config["hurt_move"] as Vec3)
                        PacketDistributor.sendToAllPlayers(SkillPayload(this, CompoundTag().apply {
                            putBoolean("hurt", true)
                            putDouble("x", sourcePos.x)
                            putDouble("y", sourcePos.y)
                            putDouble("z", sourcePos.z)
                        }))
                        triggerEvent(GuardHurt(event, hitPos))
                    }
                }
            }

            sync { data, _ ->
                if (data.getBoolean("hurt")) {
                    hurtAnim.refresh()
                    animatable.animController.setAnimation(hurtAnim, 0)
                    entity.addRelativeMovement(Vec3(data.getDouble("x"), data.getDouble("y"), data.getDouble("z")), config["hurt_move"] as Vec3)
                } else if (data.getBoolean("parry")) {
                    animatable.animController.setAnimation(parryAnim!!, 0)
                }
            }

            onEvent<SkillEvent.End> {
                guardAnim.cancel()
                entity.removeBody(guardBody.name)
            }

            onEvent<SkillEvent.Active> {
                if (canTransitionTo(SkillPhase.END) && guardAnim.isCancelled) end()
            }

            canTransitionTo = { p ->
                if (p == SkillPhase.END) {
                    hurtAnim.isCancelled && (parryAnim == null || parryAnim?.isCancelled == true)
                } else true
            }
        }
    }

}