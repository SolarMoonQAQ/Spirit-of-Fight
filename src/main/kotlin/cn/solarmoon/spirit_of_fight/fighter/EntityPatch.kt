package cn.solarmoon.spirit_of_fight.fighter

import net.minecraft.world.entity.Entity
import net.minecraft.world.level.Level
import org.ode4j.ode.DBody

open class EntityPatch(
    val entity: Entity
) {

    var weaponAttackBody: DBody? = null
    var weaponGuardBody: DBody? = null
    var isAttacking = false

    /**
     * 冻结预输入
     */
    var preInputFreeze = false

    /**
     * 冻结视角转向和移动输入 （此项对玩家有效，对一般实体仅作冻结标识，自行实现逻辑）
     */
    var moveInputFreeze = false

    /**
     * 冻结原版操作，如使用物品等
     */
    var operateFreeze = false

    open fun onJoinLevel(level: Level) {}

}