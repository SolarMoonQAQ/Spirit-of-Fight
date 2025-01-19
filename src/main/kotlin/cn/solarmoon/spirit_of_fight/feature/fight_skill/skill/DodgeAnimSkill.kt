package cn.solarmoon.spirit_of_fight.feature.fight_skill.skill

import cn.solarmoon.spark_core.SparkCore
import cn.solarmoon.spark_core.animation.IEntityAnimatable
import cn.solarmoon.spark_core.animation.anim.play.AnimInstance
import cn.solarmoon.spark_core.entity.preinput.getPreInput
import cn.solarmoon.spark_core.registry.common.SparkVisualEffects
import cn.solarmoon.spark_core.skill.Skill
import cn.solarmoon.spark_core.skill.SkillType
import cn.solarmoon.spark_core.util.MoveDirection
import cn.solarmoon.spirit_of_fight.fighter.getPatch
import net.minecraft.world.entity.player.Player
import net.minecraft.world.phys.Vec3
import net.neoforged.neoforge.event.entity.living.LivingIncomingDamageEvent

open class DodgeAnimSkill(
    animatable: IEntityAnimatable<*>,
    skillType: SkillType<IEntityAnimatable<*>, out Skill<IEntityAnimatable<*>>>,
    val animName: String,
    val switchNode: Double?,
    private val enableMove: ((AnimInstance, Vec3) -> Vec3?)
): FightSkill<IEntityAnimatable<*>>(animatable, skillType) {

    val entity get() = holder.animatable
    var direction: MoveDirection? = null
    var moveVector: Vec3 = Vec3.ZERO
    var check = true

    fun getAnimNameByDirection(direction: MoveDirection) = "${animName}_$direction"

    override fun onActivate() {
        direction?.let {
            holder.animController.setAnimation(getAnimNameByDirection(it), 0) {
                shouldTurnBody = true
            }
        }
    }

    override fun onUpdate() {
        direction?.let { d ->
            holder.animController.getPlayingAnim(getAnimNameByDirection(d))?.let {
                switchNode?.let { time -> if (it.time in time..Double.MAX_VALUE) entity.getPreInput().executeExcept("dodge") }
                enableMove.invoke(it, moveVector)?.let { entity.deltaMovement = it }

                entity.getPatch().moveInputFreeze = true

                if (it.isCancelled) end()
            } ?: end()
        }
    }

    override fun onEnd() {
        check = true
    }

    open fun onHurt(event: LivingIncomingDamageEvent) {
        direction?.let { d ->
            holder.animController.getPlayingAnim(getAnimNameByDirection(d))?.let {
                if (it.time in 0.0..0.2 && check) {
                    onPerfectDodge(it, event)
                    check = false
                }
                if (it.time in 0.0..(switchNode ?: Double.MAX_VALUE)) event.isCanceled = true
            }
        }
    }

    open fun onPerfectDodge(anim: AnimInstance, event: LivingIncomingDamageEvent) {
        SparkVisualEffects.SHADOW.addToClient(entity.id)
    }

}