package cn.solarmoon.solarmoon_core.api.blockstate

import net.minecraft.core.BlockPos
import net.minecraft.core.Direction
import net.minecraft.world.level.block.state.BlockState
import net.minecraft.world.level.block.state.properties.BedPart
import net.minecraft.world.level.block.state.properties.BlockStateProperties
import net.minecraft.world.level.block.state.properties.EnumProperty

/**
 * 像床一样的平铺方向的双格方块，此类方块必须具有FACING属性！<br/>
 * 同时需要注意，双方块本质上是两个方块，因此如果在双方块上加blockentity，实际上是加了两个blockentity，这在诸如setplace时亟需注意，因为setplace时
 * 往往只对一个pos进行操作，而容易忽略另一个pos。
 */
interface IBedPartState: IHorizontalFacingState {

    companion object {
        @JvmStatic
        val PART: EnumProperty<BedPart> = BlockStateProperties.BED_PART

        @JvmStatic
        fun getNeighbourDirection(part: BedPart, direction: Direction): Direction {
            return if (part == BedPart.FOOT) direction else direction.opposite
        }

        @JvmStatic
        fun getFootPos(state: BlockState, pos: BlockPos): BlockPos {
            val nb = getNeighbourDirection(state.getValue(PART), state.getValue(IHorizontalFacingState.FACING))
            return if (state.getValue(PART) == BedPart.FOOT) pos else pos.relative(nb)
        }
    }

}