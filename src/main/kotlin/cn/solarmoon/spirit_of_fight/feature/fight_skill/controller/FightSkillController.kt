package cn.solarmoon.spirit_of_fight.feature.fight_skill.controller

import cn.solarmoon.spark_core.SparkCore
import cn.solarmoon.spark_core.animation.IEntityAnimatable
import cn.solarmoon.spark_core.animation.preset_anim.PlayerStateAnimMachine
import cn.solarmoon.spark_core.entity.preinput.getPreInput
import cn.solarmoon.spark_core.skill.SkillController
import cn.solarmoon.spark_core.skill.getSkillControllerStateMachine
import cn.solarmoon.spark_core.util.CycleIndex
import cn.solarmoon.spirit_of_fight.data.SOFSkillTags
import cn.solarmoon.spirit_of_fight.feature.fight_skill.skill.AttackAnimSkill
import cn.solarmoon.spirit_of_fight.feature.fight_skill.skill.CommonGuardAnimSkill
import cn.solarmoon.spirit_of_fight.feature.fight_skill.skill.DodgeAnimSkill
import cn.solarmoon.spirit_of_fight.feature.fight_skill.skill.SpecialAttackAnimSkill
import cn.solarmoon.spirit_of_fight.fighter.getPatch
import net.minecraft.world.damagesource.DamageTypes
import net.minecraft.world.entity.Entity
import net.minecraft.world.entity.LivingEntity
import net.minecraft.world.entity.player.Player
import net.neoforged.neoforge.event.entity.living.LivingIncomingDamageEvent
import org.ode4j.math.DVector3
import org.ode4j.ode.DBox
import ru.nsk.kstatemachine.statemachine.processEventBlocking

abstract class FightSkillController(
    name: String,
    override val holder: LivingEntity,
    val animatable: IEntityAnimatable<*>,
    val maxComboAmount: Int
): SkillController<Entity>(name) {

    abstract val boxLength: DVector3
    abstract val boxOffset: DVector3

    var comboChanging = false
        protected set
    val comboIndex = CycleIndex(maxComboAmount, maxComboAmount - 1)

    abstract fun getComboSkill(index: Int): AttackAnimSkill

    abstract fun getGuardSkill(): CommonGuardAnimSkill

    abstract fun getSprintingAttackSkill(): AttackAnimSkill

    abstract fun getJumpAttackSkill(): AttackAnimSkill

    abstract fun getDodgeSkill(): DodgeAnimSkill

    abstract fun getSpecialAttackSkill(index: Int): SpecialAttackAnimSkill

    fun getComboSkill() = getComboSkill(comboIndex.get())

    /**
     * 设置对当前连段已改变的标识并重置连段数
     */
    open fun setComboChange() {
        comboChanging = true
        comboIndex.set(0)
    }

    override fun baseTick() {
        // 攻击碰撞大小
        holder.getPatch().weaponAttackBody?.let {
            val box = it.firstGeom as? DBox ?: return@let
            box.lengths = boxLength
            box.offsetPosition = boxOffset
        }
        // 防守碰撞大小
        holder.getPatch().weaponGuardBody?.let {
            val box = it.firstGeom as? DBox ?: return@let
            box.lengths = boxLength
            box.offsetPosition = boxOffset
        }
        super.baseTick()
    }

    override fun tick() {
        // 不在播放任意技能时重置连击
        if (!isPlaying()) {
            comboIndex.set(maxComboAmount - 1)
        }

        if (comboIndex.get() == maxComboAmount - 1 || !isPlaying()) comboChanging = false
    }

    override fun onHurt(event: LivingIncomingDamageEvent) {
        super.onHurt(event)

        getGuardSkill().onHurt(event)
        getDodgeSkill().onHurt(event)
    }

    override fun onEntry() {
        super.onEntry()
        (holder as? Player)?.getSkillControllerStateMachine()?.processEventBlocking(PlayerStateAnimMachine.ResetEvent)
    }

    override fun onExit() {
        super.onExit()
        holder.getPreInput().clear()
        if (allActiveSkills.isNotEmpty()) {
            animatable.animController.setAnimation(null, 2)
        }
        (holder as? Player)?.getSkillControllerStateMachine()?.processEventBlocking(PlayerStateAnimMachine.ResetEvent)
    }

}