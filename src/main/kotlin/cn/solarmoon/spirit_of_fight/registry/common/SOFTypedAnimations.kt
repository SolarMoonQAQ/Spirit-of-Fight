package cn.solarmoon.spirit_of_fight.registry.common

import cn.solarmoon.spark_core.animation.anim.play.TypedAnimProvider
import cn.solarmoon.spirit_of_fight.SpiritOfFight
import cn.solarmoon.spirit_of_fight.feature.hit.HitAnimationApplier
import cn.solarmoon.spirit_of_fight.fighter.getPatch
import net.minecraft.world.entity.Entity
import net.minecraft.world.entity.LivingEntity
import net.minecraft.world.entity.ai.attributes.Attributes

object SOFTypedAnimations {
    @JvmStatic
    fun register() {}

    @JvmStatic
    val HAMMER_IDLE = createStateAnim("hammer_idle")
    @JvmStatic
    val HAMMER_WALK = createMoveStateAnim("hammer_walk")
    @JvmStatic
    val HAMMER_WALK_BACK = createMoveStateAnim("hammer_walk_back")
    @JvmStatic
    val HAMMER_SPRINTING = createMoveStateAnim("hammer_sprinting")
    @JvmStatic
    val HAMMER_FALL = createStateAnim("hammer_fall")

    fun createStateAnim(name: String, provider: TypedAnimProvider = {}) = SpiritOfFight.REGISTER.typedAnimation()
        .id(name)
        .animName("EntityState/$name")
        .provider(provider)
        .build()

    fun createMoveStateAnim(name: String, provider: TypedAnimProvider = {}) = createStateAnim(name) {
        onTick {
            val holder = this.holder
            if (holder is LivingEntity) {
                speed = holder.getAttributeValue(Attributes.MOVEMENT_SPEED) / (if (holder.isSprinting) 0.13 else 0.1)
                if (holder.isUsingItem) speed /= 1.5
            }
        }
        provider.invoke(this)
    }

}