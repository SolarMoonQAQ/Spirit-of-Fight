package cn.solarmoon.spirit_of_fight.feature.fight_skill.skill

import cn.solarmoon.spark_core.animation.IEntityAnimatable
import cn.solarmoon.spark_core.entity.attack.getExtraData
import cn.solarmoon.spark_core.skill.Skill
import cn.solarmoon.spark_core.skill.SkillType
import cn.solarmoon.spirit_of_fight.feature.fight_skill.sync.ClientOperationPayload
import cn.solarmoon.spirit_of_fight.feature.fight_skill.sync.MovePayload
import cn.solarmoon.spirit_of_fight.feature.hit.getHitType
import cn.solarmoon.spirit_of_fight.fighter.getPatch
import net.minecraft.world.phys.Vec3
import net.neoforged.neoforge.event.entity.living.LivingIncomingDamageEvent
import net.neoforged.neoforge.network.PacketDistributor
import thedarkcolour.kotlinforforge.neoforge.forge.vectorutil.v3d.div

class CommonGuardAnimSkill(
    animatable: IEntityAnimatable<*>,
    skillType: SkillType<IEntityAnimatable<*>, out Skill<IEntityAnimatable<*>>>,
    animName: String,
    guardRange: Double,
    private val onUpdate: () -> Unit = {}
): GuardAnimSkill(animatable, skillType, animName, guardRange) {

    val hurtAnimName = animName + "_hurt"
    var isStanding = false
        private set
    var isBacking = false
        private set

    override fun onActivate() {
        playStandAnim()
    }

    fun playStandAnim() {
        holder.animController.setAnimation(animName, 3) {
            shouldTurnBody = true
            onTick {
                if (!holder.animController.isInTransition && isActive()) {
                    isStanding = true
                    isBacking = false
                    entity.getPatch().weaponGuardBody?.enable()
                }
            }

            onSwitch {
                if (it?.name != hurtAnimName) end()
            }
        }
    }

    fun playHurtAnim() {
        holder.animController.setAnimation(hurtAnimName, 0) {
            onTick {
                if (isActive()) {
                    isStanding = false
                    isBacking = true
                    entity.getPatch().moveInputFreeze = true

                    if (isCancelled) playStandAnim()
                }
            }

            onSwitch {
                if (it?.name != animName) end()
            }
        }
    }

    override fun onUpdate() {
        entity.getPatch().operateFreeze = true
        onUpdate.invoke()
    }

    override fun onEnd() {
        super.onEnd()
        isBacking = false
        isStanding = false
    }

    override fun onSuccessGuard(attackerPos: Vec3, event: LivingIncomingDamageEvent): Boolean {
        // 对于不可阻挡的伤害类型以及击打力度过大的情况，不会被格挡成功
        if (event.source.getExtraData()?.getHitType()?.indefensible == true) return false
        // 未在播放击退动画续上击退动画
        if (!holder.animController.isPlaying(hurtAnimName)) {
            playHurtAnim()
            PacketDistributor.sendToAllPlayers(ClientOperationPayload(entity.id, "guard_hurt", Vec3.ZERO, 0))
        }
        // 击退
        val v = Vec3(entity.x - attackerPos.x, entity.y - attackerPos.y, entity.z - attackerPos.z).normalize().div(2.5)
        MovePayload.moveEntityInClient(entity.id, v)
        return true
    }

}