package cn.solarmoon.spirit_of_fight.util

import cn.solarmoon.spark_core.animation.IEntityAnimatable
import cn.solarmoon.spark_core.animation.anim.play.AnimInstance
import cn.solarmoon.spark_core.registry.common.SparkRegistries
import cn.solarmoon.spark_core.resource.common.SparkResourcePathBuilder
import cn.solarmoon.spark_core.util.MoveDirection
import cn.solarmoon.spark_core.util.toVec3
import cn.solarmoon.spirit_of_fight.SpiritOfFight
import com.jme3.bullet.collision.ManifoldPoints
import com.jme3.bullet.collision.PhysicsCollisionObject
import com.jme3.math.Vector3f
import net.minecraft.world.damagesource.DamageSource
import net.minecraft.world.phys.Vec3

object SkillHelper {

    fun guard(damageSource: DamageSource, guardBody: PhysicsCollisionObject, onGuard: (PhysicsCollisionObject?, Vec3) -> Unit = {_, _ -> }) {
        val damagedBody = damageSource.extraData?.damagedBody
        val sourcePos = damageSource.sourcePosition

        if (guardBody.collideWithGroups != 0) {
            if (damagedBody != null && damagedBody == guardBody) {
                val hitPos = damagedBody.getPhysicsLocation(Vector3f())
                    .apply { damageSource.extraData?.manifoldId?.let { ManifoldPoints.getPositionWorldOnA(it, this) } }
                    .toVec3()
                onGuard.invoke(damagedBody, hitPos)
            } else if (sourcePos != null) {
                onGuard.invoke(null, guardBody.getPhysicsLocation(Vector3f()).toVec3())
            }
        }
    }

    fun grabToBone(animatable: IEntityAnimatable<*>, bone: String, offset: Vec3) {
        val entity = animatable.animatable
        entity.grabManager.getGrabs().forEach {
            if (!animatable.animLevel.isClientSide) {
                val pos = animatable.getWorldBonePivot(bone, offset).toVec3()
                it.setPos(pos)
            }
        }
    }

    fun createAnimByDirection(animatable: IEntityAnimatable<*>, animBaseName: String, default: MoveDirection): AnimInstance {
        val direction = animatable.animatable.moveDirection ?: default
        val animPath = SparkResourcePathBuilder.buildAnimationPath(SpiritOfFight.MOD_ID, SpiritOfFight.MOD_ID, "player", "${animBaseName}.${direction.toString().lowercase()}")
        return SparkRegistries.TYPED_ANIMATION.get(animPath)!!.create(animatable)
    }

}