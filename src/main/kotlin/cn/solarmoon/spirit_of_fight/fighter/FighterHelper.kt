package cn.solarmoon.spirit_of_fight.fighter

import cn.solarmoon.spirit_of_fight.fighter.player.PlayerPatch
import net.minecraft.world.entity.Entity
import net.minecraft.world.entity.player.Player

@Suppress("unchecked_cast")
fun Player.getPatch() = (this as IEntityPatchHolder).patch as PlayerPatch

@Suppress("unchecked_cast")
fun Entity.getPatch() = (this as IEntityPatchHolder).patch