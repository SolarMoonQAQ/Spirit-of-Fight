package cn.solarmoon.spirit_of_fight.feature.fight_skill.skill

import cn.solarmoon.spark_core.SparkCore
import cn.solarmoon.spark_core.animation.IEntityAnimatable
import cn.solarmoon.spark_core.animation.anim.play.AnimInstance
import cn.solarmoon.spark_core.entity.attack.AttackSystem
import cn.solarmoon.spark_core.entity.attack.updateAttackedData
import cn.solarmoon.spark_core.entity.getAttackAnimSpeed
import cn.solarmoon.spark_core.entity.preinput.getPreInput
import cn.solarmoon.spark_core.phys.toDQuaternion
import cn.solarmoon.spark_core.phys.toDVector3
import cn.solarmoon.spark_core.phys.toQuaternionf
import cn.solarmoon.spark_core.registry.common.SparkVisualEffects
import cn.solarmoon.spark_core.skill.Skill
import cn.solarmoon.spark_core.skill.SkillType
import cn.solarmoon.spark_core.visual_effect.common.trail.Trail
import cn.solarmoon.spirit_of_fight.feature.fight_skill.spirit.getFightSpirit
import cn.solarmoon.spirit_of_fight.feature.hit.HitType
import cn.solarmoon.spirit_of_fight.feature.hit.setHitType
import cn.solarmoon.spirit_of_fight.fighter.getPatch
import cn.solarmoon.spirit_of_fight.registry.common.SOFBodyTypes
import net.minecraft.client.Minecraft
import net.minecraft.core.Direction
import net.minecraft.core.particles.ParticleTypes
import net.minecraft.sounds.SoundEvents
import net.minecraft.sounds.SoundSource
import net.minecraft.world.entity.Entity
import net.minecraft.world.phys.Vec3
import net.neoforged.fml.loading.FMLEnvironment
import org.joml.Quaterniond
import org.joml.Vector3f
import org.ode4j.ode.DBody
import org.ode4j.ode.DBox
import org.ode4j.ode.DContactBuffer
import org.ode4j.ode.DGeom
import java.awt.Color
import kotlin.let

open class AttackAnimSkill(
    animatable: IEntityAnimatable<*>,
    skillType: SkillType<IEntityAnimatable<*>, out Skill<IEntityAnimatable<*>>>,
    val animName: String,
    private val damageMultiplier: Double,
    val baseAttackSpeed: Double,
    val switchNode: Double?,
    private val hitType: HitType,
    private val enableAttack: (AnimInstance) -> Boolean,
    private val enableMove: ((AnimInstance) -> Vec3?),
    private val onUpdate: () -> Unit = {}
): FightSkill<IEntityAnimatable<*>>(animatable, skillType) {

    val entity get() = holder.animatable
    private var attackFirstActiveCheck = true

    override fun onActivate() {
        holder.animController.setAnimation(animName, 0) {
            speed = entity.getAttackAnimSpeed(baseAttackSpeed.toFloat()).toDouble()
            shouldTurnBody = true
        }
    }

    override fun onUpdate() {
        holder.animController.getPlayingAnim(animName)?.let {
            val aBody = entity.getPatch().weaponAttackBody ?: return@let

            if (enableAttack.invoke(it)) {
                entity.getPatch().isAttacking = true
                aBody.enable()
                whenAttackActive(attackFirstActiveCheck, aBody)
                attackFirstActiveCheck = false
            } else {
                attackFirstActiveCheck = true
                aBody.disable()
            }

            entity.getPatch().moveInputFreeze = true
            entity.getPatch().operateFreeze = true

            switchNode?.let { time -> if (it.time in time..Double.MAX_VALUE) {
                entity.getPreInput().executeIfPresent()
            } }

            enableMove.invoke(it)?.let { entity.deltaMovement = it }

        } ?: end()

        onUpdate.invoke()
    }

    open fun getDamageMultiply() = damageMultiplier

    open fun getHitType() = hitType

    override fun onEnd() {
        val aBody = entity.getPatch().weaponAttackBody ?: return
        aBody.disable()
        attackFirstActiveCheck = true
    }

    /**
     * 碰撞体启用时调用
     * @param isBeginningMoment 是否在开始的一瞬间
     */
    open fun whenAttackActive(isBeginningMoment: Boolean, aBody: DBody) {
        getHitType().whenAttackActive(isBeginningMoment, aBody)
    }

    /**
     * 当击打到目标时调用（伤害触发前）
     */
    open fun whenAboutToAttack(o1: DGeom, o2: DGeom, buffer: DContactBuffer, attackSystem: AttackSystem) {
        (o2.body.owner as? Entity)?.updateAttackedData { setHitType(getHitType()) }

        getHitType().whenAboutToAttack(o1, o2, buffer, attackSystem)

        // 攻击到第一个目标调用
        if (attackSystem.attackedEntities.isEmpty()) {
            whenFirstTargetAttacked(o1, o2, buffer, attackSystem)
        }

        val contactPoint = buffer[0].contactGeom.pos
        entity.level().addParticle(ParticleTypes.SWEEP_ATTACK, contactPoint.get0(), contactPoint.get1(), contactPoint.get2(), 0.0, 0.0, 0.0)
    }

    open fun whenTargetAttacked(firstAttacked: Boolean, o1: DGeom, o2: DGeom, buffer: DContactBuffer, attackSystem: AttackSystem) {
        if (firstAttacked) modifyFightSpirit(o1, o2, buffer, attackSystem)
        getHitType().whenTargetAttacked(firstAttacked, o1, o2, buffer, attackSystem)
    }

    open fun whenFirstTargetAttacked(o1: DGeom, o2: DGeom, buffer: DContactBuffer, attackSystem: AttackSystem) {
        holder.animController.changeSpeed(2, 0.05)

        if (FMLEnvironment.dist.isClient && entity == Minecraft.getInstance().player) {
            val strength = getHitType().strength.value
            SparkVisualEffects.CAMERA_SHAKE.shake(1 + strength, 0.5f + 0.25f * strength, 0.5f + strength)
        }
    }

    open fun modifyFightSpirit(o1: DGeom, o2: DGeom, buffer: DContactBuffer, attackSystem: AttackSystem) {
        val target = o2.body.owner as? Entity ?: return
        if (target.level().isClientSide) return
        var mul = damageMultiplier
        if (o2.body.type == SOFBodyTypes.GUARD.get()) mul /= 2
        entity.getFightSpirit().addStage(mul)
    }

}