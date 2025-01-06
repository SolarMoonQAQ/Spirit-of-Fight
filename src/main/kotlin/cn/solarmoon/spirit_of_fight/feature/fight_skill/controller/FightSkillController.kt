package cn.solarmoon.spirit_of_fight.feature.fight_skill.controller

import cn.solarmoon.spark_core.SparkCore
import cn.solarmoon.spark_core.animation.IEntityAnimatable
import cn.solarmoon.spark_core.skill.SkillController
import cn.solarmoon.spark_core.util.CycleIndex
import cn.solarmoon.spirit_of_fight.data.SOFSkillTags
import cn.solarmoon.spirit_of_fight.feature.fight_skill.skill.AttackAnimSkill
import cn.solarmoon.spirit_of_fight.feature.fight_skill.skill.CommonGuardAnimSkill
import cn.solarmoon.spirit_of_fight.feature.fight_skill.skill.DodgeAnimSkill
import cn.solarmoon.spirit_of_fight.fighter.getPatch
import net.minecraft.world.damagesource.DamageTypes
import net.minecraft.world.entity.Entity
import net.minecraft.world.entity.LivingEntity
import net.neoforged.neoforge.event.entity.living.LivingIncomingDamageEvent
import org.ode4j.math.DVector3
import org.ode4j.ode.DBox

abstract class FightSkillController(
    override val holder: LivingEntity,
    val animatable: IEntityAnimatable<*>
): SkillController<Entity>() {

    abstract val boxLength: DVector3
    abstract val boxOffset: DVector3

    open val maxComboAmount = 3
    val comboIndex = CycleIndex(maxComboAmount)

    /**
     * 如果受到的伤害类型在此列表中则无法进行[onHurt]方法
     */
    val unblockableDamageTypes = hashSetOf(DamageTypes.EXPLOSION, DamageTypes.PLAYER_EXPLOSION)

    abstract fun getComboSkill(index: Int): AttackAnimSkill

    abstract fun getGuardSkill(): CommonGuardAnimSkill

    abstract fun getSprintingAttackSkill(): AttackAnimSkill

    abstract fun getJumpAttackSkill(): AttackAnimSkill

    abstract fun getDodgeSkill(): DodgeAnimSkill

    open fun isAttacking() = allSkills.any { it.isActive() && it.`is`(SOFSkillTags.FORGE_ATTACK) }

    fun getComboSkill() = getComboSkill(comboIndex.get())

    override fun tick() {
        // 不在播放任意技能时重置连击
        if (!isPlaying()) {
            comboIndex.set(maxComboAmount - 1)
        }

        // 攻击碰撞大小
        holder.getPatch().weaponAttackBody?.let {
            val box = it.body.firstGeom as? DBox ?: return@let
            box.lengths = boxLength
            box.offsetPosition = boxOffset
        }
        // 防守碰撞大小
        holder.getPatch().weaponGuardBody?.let {
            val box = it.body.firstGeom as? DBox ?: return@let
            box.lengths = boxLength
            box.offsetPosition = boxOffset
        }
    }

    override fun onHurt(event: LivingIncomingDamageEvent) {
        super.onHurt(event)

        if (unblockableDamageTypes.any { event.source.`is`(it) }) return

        getGuardSkill().onHurt(event)
        getDodgeSkill().onHurt(event)
    }

    override fun onDisabledMoment() {
        super.onDisabledMoment()
        if (allActiveSkills.isNotEmpty()) {
            animatable.animController.stopAllAnimation()
        }
    }

}