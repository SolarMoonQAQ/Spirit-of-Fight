package cn.solarmoon.spirit_of_fight.feature.fight_skill.attack

import cn.solarmoon.spark_core.SparkCore
import cn.solarmoon.spark_core.entity.attack.AttackSystem
import cn.solarmoon.spark_core.phys.attached_body.AttachedBody
import cn.solarmoon.spark_core.phys.attached_body.putBody
import cn.solarmoon.spark_core.phys.thread.PhysLevel
import cn.solarmoon.spark_core.phys.thread.getPhysLevel
import cn.solarmoon.spark_core.phys.toDVector3
import cn.solarmoon.spark_core.registry.common.SparkVisualEffects
import cn.solarmoon.spark_core.skill.getTypedSkillController
import cn.solarmoon.spirit_of_fight.feature.fight_skill.controller.FightSkillController
import net.minecraft.world.entity.Entity
import net.minecraft.world.entity.LivingEntity
import net.minecraft.world.entity.animal.IronGolem
import net.minecraft.world.level.Level
import net.neoforged.bus.api.SubscribeEvent
import net.neoforged.neoforge.event.entity.EntityJoinLevelEvent
import net.neoforged.neoforge.event.entity.living.LivingKnockBackEvent
import net.neoforged.neoforge.event.entity.player.CriticalHitEvent
import net.neoforged.neoforge.event.entity.player.SweepAttackEvent
import net.neoforged.neoforge.event.tick.EntityTickEvent
import org.ode4j.math.DVector3
import org.ode4j.ode.DBody
import org.ode4j.ode.OdeHelper

object AttackModifier {

    /**
     * 原版横扫从此再见
     */
    @SubscribeEvent
    private fun playerSweep(event: SweepAttackEvent) {
        val player = event.entity
        val skillController = player.getTypedSkillController<FightSkillController>() ?: return
        if (skillController.isAttacking()) {
            event.isSweeping = false
        }
    }

    /**
     * 使能够兼容别的模组的暴击修改，并且把原版跳劈删去
     */
    @SubscribeEvent
    private fun playerCriticalHit(event: CriticalHitEvent) {
        val player = event.entity
        val skillController = player.getTypedSkillController<FightSkillController>() ?: return
        if (skillController.isAttacking()) {
            // 逻辑是原版暴击只能在跳劈情况下触发，因此直接删掉原版跳劈，但是别的模组由暴击率驱动的概率性伤害显然理应不受其影响
            if (event.vanillaMultiplier == 1.5f) event.isCriticalHit = false
        }
    }

    /**
     * 取消默认情况下击退的y轴向量
     */
    @SubscribeEvent
    private fun knockBackModify(event: LivingKnockBackEvent) {
        val entity = event.entity
        entity.setOnGround(false)
    }

    @SubscribeEvent
    private fun test(event: EntityJoinLevelEvent) {
        val entity = event.entity
        if (entity is IronGolem) {
            entity.putBody(AttackBody(event.level, entity))
        }
    }

    @SubscribeEvent
    private fun test2(event: EntityTickEvent.Pre) {
        val entity = event.entity
        val level = entity.level()
    }

    class AttackBody(level: Level, entity: Entity): AttachedBody {
        override val name: String = "wow"
        override val physLevel: PhysLevel = level.getPhysLevel()
        override val body: DBody = OdeHelper.createBody(name, entity, false, physLevel.physWorld.world)
        val geom = OdeHelper.laterCreateBox(body, physLevel.physWorld, DVector3())
        val ats = AttackSystem(entity).apply { ignoreInvulnerableTime = false }

        init {
            body.onTick {
                val bb = entity.boundingBox
                geom.lengths = DVector3(bb.xsize, bb.ysize, bb.zsize)
                body.position = bb.center.toDVector3()
                if (level.isClientSide) SparkVisualEffects.GEOM.getRenderableBox(geom.uuid.toString()).refresh(geom)
            }

            geom.onCollide { o2, buffer ->
                val target = o2.body.owner
                if (target is LivingEntity) {
                    ats.customGeomAttack(geom, o2) { target.hurt(target.damageSources().magic(), 1f) }
                }
                ats.reset()
            }
        }
    }

}