package cn.solarmoon.spirit_of_fight.feature.fight_skill.skill

import cn.solarmoon.spark_core.animation.IEntityAnimatable
import cn.solarmoon.spark_core.animation.anim.auto_anim.EntityStateAutoAnim
import cn.solarmoon.spark_core.animation.anim.auto_anim.getAutoAnim
import cn.solarmoon.spark_core.animation.anim.play.MixedAnimation
import cn.solarmoon.spark_core.entity.attack.clearAttackedData
import cn.solarmoon.spark_core.entity.preinput.getPreInput
import cn.solarmoon.spark_core.skill.Skill
import cn.solarmoon.spark_core.skill.SkillType
import cn.solarmoon.spirit_of_fight.feature.fight_skill.sync.ClientOperationPayload
import cn.solarmoon.spirit_of_fight.feature.fight_skill.sync.MovePayload
import cn.solarmoon.spirit_of_fight.fighter.getPatch
import net.minecraft.world.phys.Vec3
import net.neoforged.neoforge.event.entity.living.LivingIncomingDamageEvent
import net.neoforged.neoforge.network.PacketDistributor
import thedarkcolour.kotlinforforge.neoforge.forge.vectorutil.v3d.div

class CommonGuardAnimSkill(
    animatable: IEntityAnimatable<*>,
    skillType: SkillType<IEntityAnimatable<*>, out Skill<IEntityAnimatable<*>>>,
    animName: String,
    guardRange: Double
): GuardAnimSkill(animatable, skillType, animName, guardRange) {

    val hurtAnimName = animName + "_hurt"
    var isStanding = false
        private set
    var isBacking = false
        private set

    override fun onActivate() {
        holder.animController.stopAndAddAnimation(MixedAnimation(animName, startTransSpeed = 2.5f).apply { shouldTurnBody = true })
    }

    override fun onUpdate() {
        holder.animData.playData.getMixedAnimation(animName)?.let {
            if (!it.isInTransition) {
                isStanding = true
                entity.getPatch().weaponGuardBody?.enable()
            } else isStanding = false

            isBacking = false

            // 和走路混合
            holder.getAutoAnim<EntityStateAutoAnim>("EntityState")?.blendWithoutArms(false) { it.name == animName }
        } ?: run {
            // 击退后再续上动作
            holder.animData.playData.getMixedAnimation(hurtAnimName)?.let {
                isBacking = true
                if (it.isCancelled) holder.animController.stopAndAddAnimation(MixedAnimation(animName, startTransSpeed = 6f))
            } ?: run {
                entity.getPatch().weaponGuardBody?.disable()
                isBacking = false
                active = false
            }
            isStanding = false
        }
    }

    override fun onEnd() {
        super.onEnd()
        isBacking = false
        isStanding = false
    }

    override fun onSuccessGuard(attackerPos: Vec3, event: LivingIncomingDamageEvent): Boolean {
        if (!holder.animController.isPlaying(hurtAnimName) { !it.isCancelled }) {
            playHurtAnim()
            PacketDistributor.sendToAllPlayers(ClientOperationPayload(entity.id, "guard_hurt", Vec3.ZERO, 0))
        }
        val v = Vec3(entity.x - attackerPos.x, entity.y - attackerPos.y, entity.z - attackerPos.z).normalize().div(2.5)
        MovePayload.moveEntityInClient(entity.id, v)
        entity.clearAttackedData()
        return true
    }

    fun playHurtAnim() {
        holder.animController.stopAndAddAnimation(MixedAnimation(hurtAnimName, startTransSpeed = 6f))
    }

}