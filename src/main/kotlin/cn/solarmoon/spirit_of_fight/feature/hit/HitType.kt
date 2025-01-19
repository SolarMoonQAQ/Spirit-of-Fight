package cn.solarmoon.spirit_of_fight.feature.hit

import cn.solarmoon.spark_core.animation.IAnimatable
import cn.solarmoon.spark_core.animation.IEntityAnimatable
import cn.solarmoon.spark_core.animation.anim.play.AnimInstance
import cn.solarmoon.spark_core.entity.attack.AttackSystem
import cn.solarmoon.spark_core.phys.toDQuaternion
import cn.solarmoon.spark_core.phys.toDVector3
import cn.solarmoon.spark_core.phys.toQuaternionf
import cn.solarmoon.spark_core.registry.common.SparkRegistries
import cn.solarmoon.spark_core.registry.common.SparkVisualEffects
import cn.solarmoon.spark_core.skill.Skill
import cn.solarmoon.spark_core.skill.SkillType
import cn.solarmoon.spark_core.util.Side
import cn.solarmoon.spark_core.visual_effect.common.trail.Trail
import cn.solarmoon.spirit_of_fight.registry.common.SOFRegistries
import net.minecraft.core.Direction
import net.minecraft.sounds.SoundEvents
import net.minecraft.sounds.SoundSource
import net.minecraft.tags.TagKey
import net.minecraft.world.entity.Entity
import org.joml.Quaterniond
import org.joml.Vector3f
import org.ode4j.ode.DBody
import org.ode4j.ode.DBox
import org.ode4j.ode.DContactBuffer
import org.ode4j.ode.DGeom
import java.awt.Color

interface HitType {

    val strength: AttackStrength

    val isHeavy get() = strength.value > 1

    /**
     * 此值为true时，该攻击无法被默认格挡阻挡，并且受击动画不会被任何动画覆盖
     */
    val indefensible: Boolean

    /**
     * 例：攻击者发造成攻击时，攻击者若在受击者的正前方，那么[posSide]会返回[Side.FRONT]，如果击打到了左腿，那么[boneName]大概会返回["leftLeg"]，如果攻击是从以受击者视角的左边袭来的，那么[hitSide]会返回[Side.LEFT]
     * @param target 受击目标
     * @param strength 受击力度
     * @param boneName 受击骨骼
     * @param posSide 攻击者所在的相对于受击者的四个朝向（前后左右）
     * @param hitSide 攻击碰撞所在的相对于受击者的**左右**方向
     */
    fun getHitAnimation(target: IAnimatable<*>, strength: AttackStrength, boneName: String, posSide: Side, hitSide: Side): AnimInstance?

    /**
     * 碰撞体启用时调用
     * @param isBeginningMoment 是否在开始的一瞬间
     */
    fun whenAttackActive(isBeginningMoment: Boolean, aBody: DBody) {
        val entity = aBody.owner as? Entity ?: return
        if (isBeginningMoment) entity.level().playSound(null, entity.blockPosition().above(), SoundEvents.PLAYER_ATTACK_SWEEP, SoundSource.PLAYERS, 0.75f, 1f - strength.value * 0.5f / 3f)

        val animatable = entity as? IEntityAnimatable<*> ?: return
        val box = aBody.firstGeom as? DBox ?: return
        val color = if (strength == AttackStrength.SUPER_HEAVY) Color.RED else Color.WHITE
        if (entity.level().isClientSide) {
            SparkVisualEffects.TRAIL.refresh(box.uuid.toString()) {
                box.quaternion = animatable.getWorldBoneMatrix(aBody.name, it).getUnnormalizedRotation(Quaterniond()).toDQuaternion()
                box.position = animatable.getWorldBonePivot(aBody.name, it).add(Vector3f(0f, 0f, -box.lengths.get2().toFloat() / 2).rotate(box.quaternion.toQuaternionf())).toDVector3()
                Trail(box, Direction.Axis.Z, color).apply {
                    entity.weaponItem?.let { setTexture(it) }
                }
            }
        }
    }

    /**
     * 当击打到目标时调用（伤害触发前）
     */
    fun whenAboutToAttack(o1: DGeom, o2: DGeom, buffer: DContactBuffer, attackSystem: AttackSystem)

    /**
     * 攻击目标的指令已经执行完成后调用
     */
    fun whenTargetAttacked(firstAttacked: Boolean, o1: DGeom, o2: DGeom, buffer: DContactBuffer, attackSystem: AttackSystem)

    val simpleName get() = registryKey.path

    val id get() = SOFRegistries.HIT_TYPE.getId(this)

    val registryKey get() = SOFRegistries.HIT_TYPE.getKey(this) ?: throw NullPointerException("${::javaClass.name} 尚未注册")

    val resourceKey get() = SOFRegistries.HIT_TYPE.getResourceKey(this).get()

    val builtInRegistryHolder get() = SOFRegistries.HIT_TYPE.getHolder(resourceKey).get()

    fun `is`(tag: TagKey<HitType>) = builtInRegistryHolder.`is`(tag)

}