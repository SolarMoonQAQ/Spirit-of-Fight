package cn.solarmoon.spirit_of_fight.feature.fight_skill.sync

import cn.solarmoon.spark_core.SparkCore
import cn.solarmoon.spark_core.data.SerializeHelper
import cn.solarmoon.spark_core.skill.getTypedSkillController
import cn.solarmoon.spark_core.util.MoveDirection
import cn.solarmoon.spirit_of_fight.feature.fight_skill.controller.FightSkillController
import cn.solarmoon.spirit_of_fight.feature.fight_skill.controller.SwordFightSkillController
import cn.solarmoon.spirit_of_fight.fighter.getPatch
import net.minecraft.network.RegistryFriendlyByteBuf
import net.minecraft.network.codec.StreamCodec
import net.minecraft.network.protocol.common.custom.CustomPacketPayload
import net.minecraft.resources.ResourceLocation
import net.minecraft.server.level.ServerPlayer
import net.minecraft.world.entity.LivingEntity
import net.minecraft.world.phys.Vec3
import net.neoforged.neoforge.network.PacketDistributor
import net.neoforged.neoforge.network.handling.IPayloadContext
import kotlin.math.PI
import kotlin.math.cos
import kotlin.math.sin

data class ClientOperationPayload(
    val entityId: Int,
    val operation: String,
    val moveVector: Vec3,
    val id: Int
): CustomPacketPayload {

    override fun type(): CustomPacketPayload.Type<out CustomPacketPayload?> {
        return TYPE
    }

    companion object {
        @JvmStatic
        fun handle(payload: ClientOperationPayload, context: IPayloadContext) {
            val player = context.player()
            val entity = context.player().level().getEntity(payload.entityId) ?: return
            val skillController = entity.getTypedSkillController<FightSkillController>() ?: return
            when(payload.operation) {
                "combo" -> {
                    skillController.comboIndex.set(payload.id)
                    skillController.getComboSkill().activate()
                }
                "sprinting_attack" -> {
                    skillController.getSprintingAttackSkill().activate()
                }
                "jump_attack" -> {
                    skillController.getJumpAttackSkill().activate()
                }
                "guard" -> {
                    skillController.getGuardSkill().activate()
                }
                "guard_stop" -> {
                    skillController.getGuardSkill().end()
                }
                "guard_hurt" -> {
                    skillController.getGuardSkill().playHurtAnim()
                }
                "dodge" -> {
                    val dodge = skillController.getDodgeSkill()
                    dodge.direction = MoveDirection.getById(payload.id)
                    dodge.moveVector = payload.moveVector
                    dodge.activate()
                }
                "parry" -> {
                    (skillController as? SwordFightSkillController)?.getParrySkill()?.activate()
                }
                "parried" -> {
                    (skillController as? SwordFightSkillController)?.getParrySkill()?.playParriedAnim(payload.id)
                }
            }
            if (player is ServerPlayer) PacketDistributor.sendToPlayersNear(player.serverLevel(), player, player.x, player.y, player.z, 512.0, payload)
        }

        @JvmStatic
        val TYPE = CustomPacketPayload.Type<ClientOperationPayload>(ResourceLocation.fromNamespaceAndPath(SparkCore.MOD_ID, "client_operation"))

        @JvmStatic
        val STREAM_CODEC = object : StreamCodec<RegistryFriendlyByteBuf, ClientOperationPayload> {
            override fun decode(buffer: RegistryFriendlyByteBuf): ClientOperationPayload {
                val entityId = buffer.readInt()
                val operation = buffer.readUtf()
                val moveVector = SerializeHelper.VEC3_STREAM_CODEC.decode(buffer)
                val moveDirection = buffer.readInt()
                return ClientOperationPayload(entityId, operation, moveVector, moveDirection)
            }

            override fun encode(buffer: RegistryFriendlyByteBuf, value: ClientOperationPayload) {
                buffer.writeInt(value.entityId)
                buffer.writeUtf(value.operation)
                SerializeHelper.VEC3_STREAM_CODEC.encode(buffer, value.moveVector)
                buffer.writeInt(value.id)
            }
        }
    }

}