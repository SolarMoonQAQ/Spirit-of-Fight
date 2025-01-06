package cn.solarmoon.spirit_of_fight.feature.fight_skill.skill

import cn.solarmoon.spark_core.SparkCore
import cn.solarmoon.spark_core.animation.IEntityAnimatable
import cn.solarmoon.spark_core.entity.attack.getAttackedData
import cn.solarmoon.spark_core.entity.state.canSee
import cn.solarmoon.spark_core.phys.toVec3
import cn.solarmoon.spark_core.registry.common.SparkVisualEffects
import cn.solarmoon.spark_core.skill.BaseSkill
import cn.solarmoon.spark_core.skill.Skill
import cn.solarmoon.spark_core.skill.SkillType
import cn.solarmoon.spirit_of_fight.fighter.getPatch
import net.minecraft.world.phys.Vec3
import net.neoforged.neoforge.event.entity.living.LivingIncomingDamageEvent

abstract class GuardAnimSkill(
    animatable: IEntityAnimatable<*>,
    skillType: SkillType<IEntityAnimatable<*>, out Skill<IEntityAnimatable<*>>>,
    val animName: String,
    val guardRange: Double
): BaseSkill<IEntityAnimatable<*>>(animatable, skillType) {

    val entity = animatable.animatable

    override fun onEnd() {
        entity.getPatch().weaponGuardBody?.disable()
        holder.animController.stopAllAnimation()
    }

    open fun onHurt(event: LivingIncomingDamageEvent) {
        if (isActive() && entity.getPatch().weaponGuardBody?.body?.isEnabled == true) {
            val damageSource = event.source
            SparkVisualEffects.CAMERA_SHAKE.shakeToClient(entity, 2, 0.5f)
            // 对于原版生物，只要在一个扇形范围内即可，对于lib的obb碰撞，则判断是否相交，同时如果受击数据不为空，那么以受击数据为准
            val attackedData = entity.getAttackedData()
            // 对于不可阻挡的伤害类型以及击打力度大于0的情况，不会被格挡成功

            // 如果受击数据里有guard，则免疫此次攻击
            val isBoxInteract = attackedData != null && attackedData.damagedBody?.name == entity.getPatch().weaponGuardBody?.name
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