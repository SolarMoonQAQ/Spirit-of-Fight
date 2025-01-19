package cn.solarmoon.spirit_of_fight.feature.fight_skill.skill

import cn.solarmoon.spark_core.SparkCore
import cn.solarmoon.spark_core.animation.IEntityAnimatable
import cn.solarmoon.spark_core.entity.attack.getExtraData
import cn.solarmoon.spark_core.entity.canSee
import cn.solarmoon.spark_core.phys.toVec3
import cn.solarmoon.spark_core.registry.common.SparkVisualEffects
import cn.solarmoon.spark_core.skill.Skill
import cn.solarmoon.spark_core.skill.SkillType
import cn.solarmoon.spirit_of_fight.fighter.getPatch
import cn.solarmoon.spirit_of_fight.registry.common.SOFBodyTypes
import net.minecraft.world.damagesource.DamageTypes
import net.minecraft.world.phys.Vec3
import net.neoforged.neoforge.event.entity.living.LivingIncomingDamageEvent

abstract class GuardAnimSkill(
    animatable: IEntityAnimatable<*>,
    skillType: SkillType<IEntityAnimatable<*>, out Skill<IEntityAnimatable<*>>>,
    val animName: String,
    val guardRange: Double
): FightSkill<IEntityAnimatable<*>>(animatable, skillType) {

    val entity get() = holder.animatable
    /**
     * 如果受到的伤害类型在此列表中则无法进行[onHurt]方法
     */
    open val unblockableDamageTypes = hashSetOf(DamageTypes.EXPLOSION, DamageTypes.PLAYER_EXPLOSION)

    override fun onEnd() {
        entity.getPatch().weaponGuardBody?.disable()
        holder.animController.stopAnimation()
    }

    open fun onHurt(event: LivingIncomingDamageEvent) {
        if (unblockableDamageTypes.any { event.source.`is`(it) }) return
        if (isActive() && entity.getPatch().weaponGuardBody?.isEnabled == true) {
            val damageSource = event.source
            SparkVisualEffects.CAMERA_SHAKE.shakeToClient(entity, 2, 0.5f)
            // 对于原版生物，只要在一个扇形范围内即可，对于lib的obb碰撞，则判断是否相交，同时如果受击数据不为空，那么以受击数据为准
            val attackedData = damageSource.getExtraData()
            // 如果受击数据里有guard，则免疫此次攻击
            val isBoxInteract = attackedData != null && attackedData.damagedBody?.type == SOFBodyTypes.GUARD.get()
            // 如果受到box的攻击，位移以box中心为准，否则以直接攻击者的坐标位置为准
            val targetPos = attackedData?.damageBox?.position?.toVec3() ?: damageSource.sourcePosition ?: return
            // 如果受到box的攻击，按防守盒是否被碰撞为准，否则以攻击者的坐标位置是否在指定扇形范围内为准
            val attackedCheck = if (attackedData != null) isBoxInteract else entity.canSee(targetPos, guardRange)
            if (attackedCheck) {
                event.isCanceled = onSuccessGuard(targetPos, event)
            }
        }
    }

    abstract fun onSuccessGuard(attackerPos: Vec3, event: LivingIncomingDamageEvent): Boolean

}