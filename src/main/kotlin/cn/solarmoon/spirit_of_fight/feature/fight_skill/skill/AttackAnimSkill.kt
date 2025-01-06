package cn.solarmoon.spirit_of_fight.feature.fight_skill.skill

import cn.solarmoon.spark_core.SparkCore
import cn.solarmoon.spark_core.animation.IEntityAnimatable
import cn.solarmoon.spark_core.animation.anim.play.MixedAnimation
import cn.solarmoon.spark_core.entity.attack.AttackSystem
import cn.solarmoon.spark_core.entity.preinput.getPreInput
import cn.solarmoon.spark_core.entity.state.getAttackAnimSpeed
import cn.solarmoon.spark_core.phys.attached_body.AttachedBody
import cn.solarmoon.spark_core.phys.attached_body.EntityAnimatedAttackBody
import cn.solarmoon.spark_core.phys.toDQuaternion
import cn.solarmoon.spark_core.phys.toDVector3
import cn.solarmoon.spark_core.phys.toQuaternionf
import cn.solarmoon.spark_core.registry.common.SparkVisualEffects
import cn.solarmoon.spark_core.skill.BaseSkill
import cn.solarmoon.spark_core.skill.Skill
import cn.solarmoon.spark_core.skill.SkillType
import cn.solarmoon.spark_core.visual_effect.common.trail.Trail
import cn.solarmoon.spirit_of_fight.fighter.getPatch
import net.minecraft.core.Direction
import net.minecraft.world.phys.Vec3
import org.joml.Quaterniond
import org.joml.Vector3f
import org.ode4j.ode.DContactBuffer
import org.ode4j.ode.DGeom
import kotlin.let

open class AttackAnimSkill(
    animatable: IEntityAnimatable<*>,
    skillType: SkillType<IEntityAnimatable<*>, out Skill<IEntityAnimatable<*>>>,
    val animName: String,
    val baseAttackSpeed: Double,
    val switchNode: Double?,
    private val enableAttack: (MixedAnimation) -> Boolean,
    private val enableMove: ((MixedAnimation) -> Vec3?)
): BaseSkill<IEntityAnimatable<*>>(animatable, skillType) {

    val entity = animatable.animatable

    override fun onActivate() {
        val anim = MixedAnimation(animName, startTransSpeed = 6f, speed = entity.getAttackAnimSpeed(baseAttackSpeed.toFloat())).apply { shouldTurnBody = true }
        holder.animController.stopAndAddAnimation(anim)
        anim.isCancelled = false
    }

    override fun onUpdate() {
        val aBody = entity.getPatch().weaponAttackBody ?: return
        val anim = holder.animData.playData.getMixedAnimation(animName)
        anim?.let {
            if (enableAttack.invoke(it)) {
                aBody.enable()
                whenAttackActive(aBody)
            } else {
                aBody.disable()
            }

            switchNode?.let { time -> if (it.isTickIn(time, Double.MAX_VALUE)) entity.getPreInput().executeIfPresent() }

            enableMove.invoke(it)?.let { entity.deltaMovement = it }

            if (it.isCancelled) {
                end()
            }

        } ?: run {
            end()
        }
    }

    override fun onEnd() {
        val aBody = entity.getPatch().weaponAttackBody ?: return
        aBody.disable()
    }

    /**
     * 碰撞体启用时调用
     */
    open fun whenAttackActive(aBody: AttachedBody) {
        if (aBody is EntityAnimatedAttackBody) {
            val box = aBody.geom
            if (entity.level().isClientSide) {
                SparkVisualEffects.TRAIL.refresh(box.uuid.toString()) {
                    box.quaternion = holder.getBoneMatrix(aBody.boneName, it).getUnnormalizedRotation(Quaterniond()).toDQuaternion()
                    box.position = holder.getBonePivot(aBody.boneName, it).add(Vector3f(0f, 0f, -box.lengths.get2().toFloat() / 2).rotate(box.quaternion.toQuaternionf())).toDVector3()
                    Trail(box, Direction.Axis.Z).apply {
                        entity.weaponItem?.let { setTexture(it) }
                    }
                }
            }
        }
    }

    /**
     * 当击打到目标时调用（伤害触发前）
     */
    open fun whenAttacked(o1: DGeom, o2: DGeom, buffer: DContactBuffer, attackSystem: AttackSystem) {
        // 攻击到第一个目标调用
        if (attackSystem.attackedEntities.size == 1) {
            whenFirstAttacked(o1, o2, buffer, attackSystem)
        }
    }

    open fun whenFirstAttacked(o1: DGeom, o2: DGeom, buffer: DContactBuffer, attackSystem: AttackSystem) {
        holder.animController.startFreezing(false)
    }

}