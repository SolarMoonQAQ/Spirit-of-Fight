package cn.solarmoon.spirit_of_fight.fighter.player

import cn.solarmoon.spark_core.animation.IEntityAnimatable
import cn.solarmoon.spark_core.event.EntityTurnEvent
import cn.solarmoon.spark_core.event.PlayerRenderAnimInFirstPersonEvent
import cn.solarmoon.spark_core.skill.getTypedSkillController
import cn.solarmoon.spirit_of_fight.feature.fight_skill.controller.FightSkillController
import cn.solarmoon.spirit_of_fight.feature.fight_skill.skill.AttackAnimSkill
import cn.solarmoon.spirit_of_fight.feature.lock_on.LockOnController
import cn.solarmoon.spirit_of_fight.fighter.getPatch
import net.minecraft.client.player.LocalPlayer
import net.minecraft.util.Mth
import net.minecraft.world.entity.player.Player
import net.neoforged.bus.api.SubscribeEvent
import net.neoforged.neoforge.client.event.ViewportEvent
import org.joml.Vector2f

class CameraAdjuster {

    companion object {
        @JvmStatic
        val CAMERA_TURN: Vector2f = Vector2f()
    }

    @SubscribeEvent
    private fun lockHeadTurn(event: EntityTurnEvent) {
        val entity = event.entity
        val xRot = event.xRot.toFloat()
        val yRot = event.yRot.toFloat()
        if (entity is IEntityAnimatable<*>) {
            val flag1 = entity.getPatch().moveInputFreeze == true && entity.animController.getPlayingAnim()?.time != 0.0
            if (flag1) {
                if (entity is LocalPlayer && !LockOnController.hasTarget) CAMERA_TURN.add(xRot, yRot)
                event.isCanceled = true
            } else if (CAMERA_TURN != Vector2f()) {
                val x = CAMERA_TURN.x.toDouble()
                val y = CAMERA_TURN.y.toDouble()
                CAMERA_TURN.set(0f)
                entity.turn(y, x)
            }
        }
    }

    @SubscribeEvent
    private fun offsetCameraWhenLock(event: ViewportEvent.ComputeCameraAngles) {
        if (CAMERA_TURN != Vector2f()) {
            val f = CAMERA_TURN.x * 0.15f
            val f1 = CAMERA_TURN.y * 0.15f
            val entity = event.camera.entity
            event.yaw = entity.yRot + f1
            event.pitch = Mth.clamp(entity.xRot + f, -90f, 90f)
        }
    }

    @SubscribeEvent
    private fun renderAnimInFirstPersonWhenAttack(event: PlayerRenderAnimInFirstPersonEvent) {
        val player = event.player
        val sc = player.getTypedSkillController<FightSkillController>()
        event.shouldRender = sc?.allActiveSkills?.any { it is AttackAnimSkill } == true || sc?.getGuardSkill()?.isActive() == true
    }

}