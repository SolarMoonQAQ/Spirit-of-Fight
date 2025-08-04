package cn.solarmoon.spirit_of_fight.poise_system

import net.minecraft.world.entity.Entity
import net.neoforged.neoforge.network.PacketDistributor

class PoiseData(
    val entity: Entity,
    var currentValue: Int = 100,
    var maxValue: Int = 100,
) {

    val isBroken get() = currentValue <= 0

    fun reduce(value: Int): Boolean {
        if (!entity.level().isClientSide) {
            currentValue -= value
            clientSync()
            if (isBroken) {
                reset()
                clientReset()
                return true
            }
        }
        return false
    }

    fun reset() {
        currentValue = maxValue
    }

    fun clientSync() {
        PacketDistributor.sendToAllPlayers(PoiseSetPayload(entity.id, currentValue, maxValue))
    }

    fun clientReset() {
        PacketDistributor.sendToAllPlayers(PoiseResetPayload(entity.id))
    }

}