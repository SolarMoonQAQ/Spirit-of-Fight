package cn.solarmoon.spirit_of_fight.fighter

import cn.solarmoon.spark_core.phys.attached_body.AttachedBody
import cn.solarmoon.spark_core.util.MoveDirection
import net.minecraft.world.entity.Entity
import net.minecraft.world.phys.Vec3

open class EntityPatch(
    val entity: Entity
) {

    var weaponAttackBody: AttachedBody? = null
    var weaponGuardBody: AttachedBody? = null

}