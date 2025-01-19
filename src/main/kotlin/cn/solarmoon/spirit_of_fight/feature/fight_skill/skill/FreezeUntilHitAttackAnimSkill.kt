package cn.solarmoon.spirit_of_fight.feature.fight_skill.skill

import cn.solarmoon.spark_core.animation.IEntityAnimatable
import cn.solarmoon.spark_core.animation.anim.play.AnimInstance
import cn.solarmoon.spark_core.entity.attack.AttackSystem
import cn.solarmoon.spark_core.entity.preinput.getPreInput
import cn.solarmoon.spark_core.skill.Skill
import cn.solarmoon.spark_core.skill.SkillType
import cn.solarmoon.spirit_of_fight.feature.hit.HitType
import net.minecraft.sounds.SoundEvents
import net.minecraft.sounds.SoundSource
import net.minecraft.world.phys.Vec3
import org.ode4j.ode.DContactBuffer
import org.ode4j.ode.DGeom

/**
 * 直到攻击到第一个目标才能在该技能中触发预输入
 */
open class FreezeUntilHitAttackAnimSkill(
    animatable: IEntityAnimatable<*>,
    skillType: SkillType<IEntityAnimatable<*>, out Skill<IEntityAnimatable<*>>>,
    animName: String,
    damageMultiplier: Double,
    baseAttackSpeed: Double,
    hitType: HitType,
    enableAttack: (AnimInstance) -> Boolean,
    enableMove: ((AnimInstance) -> Vec3?),
    onUpdate: () -> Unit = {}
): AttackAnimSkill(animatable, skillType, animName, damageMultiplier, baseAttackSpeed, null, hitType, enableAttack, enableMove, onUpdate) {

    var shouldFreeze = true
        private set

    override fun onActivate() {
        super.onActivate()
        shouldFreeze = true
    }

    override fun onUpdate() {
        super.onUpdate()
        holder.animController.getPlayingAnim(animName)?.let {
            if (!shouldFreeze) entity.getPreInput().executeIfPresent()
        }
    }

    override fun end() {
        super.end()
        entity.getPreInput().clearIfPresent("combo_change")
    }

    override fun whenFirstTargetAttacked(o1: DGeom, o2: DGeom, buffer: DContactBuffer, attackSystem: AttackSystem) {
        super.whenFirstTargetAttacked(o1, o2, buffer, attackSystem)
        shouldFreeze = false
        entity.level().playSound(null, entity.onPos.above(), SoundEvents.PLAYER_ATTACK_KNOCKBACK, SoundSource.PLAYERS, 1.0f, 0.75f)
    }

}