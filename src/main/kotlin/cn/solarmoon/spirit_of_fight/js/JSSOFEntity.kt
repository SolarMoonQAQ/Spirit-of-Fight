package cn.solarmoon.spirit_of_fight.js

import cn.solarmoon.spark_core.animation.IEntityAnimatable
import cn.solarmoon.spark_core.js.extension.JSEntity
import cn.solarmoon.spirit_of_fight.entity.WieldStyle
import cn.solarmoon.spirit_of_fight.spirit.getFightSpirit
import net.minecraft.world.entity.Entity

interface JSSOFEntity: JSEntity {

    fun addFightSpirit(value: Int) {
        val level = entity.level()
        if (!level.isClientSide) {
            val fightSpirit = entity.getFightSpirit()
            fightSpirit.addStage(value)
            fightSpirit.syncToClient(entity.id)
        }
    }

    fun getFightSpirit() = entity.getFightSpirit()

    fun clearGrabs() = entity.grabManager.clear()

    fun blendMove() {
        val entity = entity
        if (entity !is IEntityAnimatable<*>) return
        if (entity.isMoving) {
//            entity.animController.blendAnimation(BlendAnimation(entity.createAnimation(1)))
        }
    }

    fun toggleWieldStyle() {
        WieldStyle.switch(entity)
    }

    override fun commonAttack(target: Entity) {
        target.pushHurtData(target.hurtData)
        super.commonAttack(target)
    }

}