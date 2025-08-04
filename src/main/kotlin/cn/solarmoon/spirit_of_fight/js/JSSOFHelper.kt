package cn.solarmoon.spirit_of_fight.js

import cn.solarmoon.spark_core.animation.IEntityAnimatable
import cn.solarmoon.spark_core.animation.anim.play.AnimInstance
import cn.solarmoon.spark_core.js.JSComponent
import cn.solarmoon.spark_core.js.call
import cn.solarmoon.spark_core.util.MoveDirection
import cn.solarmoon.spirit_of_fight.util.SkillHelper
import com.jme3.bullet.collision.PhysicsCollisionObject
import net.minecraft.world.damagesource.DamageSource
import net.minecraft.world.phys.Vec3
import org.mozilla.javascript.Function

object JSSOFHelper: JSComponent() {

    fun guard(damageSource: DamageSource, guardBody: PhysicsCollisionObject, onGuard: Function) {
        SkillHelper.guard(damageSource, guardBody) { a, b -> onGuard.call(engine, a, b) }
    }

    fun grabToBone(animatable: IEntityAnimatable<*>, bone: String, offset: Vec3) {
        SkillHelper.grabToBone(animatable, bone, offset)
    }

    fun createAnimByDirection(animatable: IEntityAnimatable<*>, animBaseName: String, default: String): AnimInstance {
        return SkillHelper.createAnimByDirection(animatable, animBaseName, MoveDirection.valueOf(default.uppercase()))
    }

}