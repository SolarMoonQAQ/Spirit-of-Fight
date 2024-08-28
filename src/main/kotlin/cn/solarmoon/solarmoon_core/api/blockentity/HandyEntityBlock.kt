package cn.solarmoon.solarmoon_core.api.blockentity

import cn.solarmoon.solarmoon_core.registry.common.CommonAttachments
import com.mojang.serialization.MapCodec
import net.minecraft.core.BlockPos
import net.minecraft.world.level.BlockGetter
import net.minecraft.world.level.Level
import net.minecraft.world.level.block.BaseEntityBlock
import net.minecraft.world.level.block.RenderShape
import net.minecraft.world.level.block.entity.BlockEntity
import net.minecraft.world.level.block.entity.BlockEntityTicker
import net.minecraft.world.level.block.entity.BlockEntityType
import net.minecraft.world.level.block.state.BlockState
import net.minecraft.world.phys.shapes.CollisionContext
import net.minecraft.world.phys.shapes.VoxelShape

abstract class HandyEntityBlock(properties: Properties): BaseEntityBlock(properties) {

    /**
     * 必填项 - 碰撞箱
     */
    abstract override fun getShape(state: BlockState, level: BlockGetter, pos: BlockPos, context: CollisionContext): VoxelShape

    /**
     * 设置绑定的方块实体
     *
     * 同时决定ticker所对应的实体类型（具体到注册类）
     */
    abstract fun getBlockEntityType(): BlockEntityType<*>

    override fun newBlockEntity(pos: BlockPos, state: BlockState): BlockEntity? {
        return getBlockEntityType().create(pos, state)
    }

    /**
     * 默认有模型
     */
    override fun getRenderShape(pState: BlockState): RenderShape {
        return RenderShape.MODEL
    }

    /**
     * 默认直接解码自己
     */
    override fun codec(): MapCodec<out BaseEntityBlock> {
        return simpleCodec { this }
    }

    /**
     * 启用红石信号
     */
    override fun hasAnalogOutputSignal(state: BlockState): Boolean {
        return true
    }

    override fun <T : BlockEntity?> getTicker(level: Level, state: BlockState, type: BlockEntityType<T>): BlockEntityTicker<T>? {
        return createTickerHelper(type, getBlockEntityType(), this::tick)
    }

    open fun tick(level: Level, pos: BlockPos, state: BlockState, blockEntity: BlockEntity) {
        // 进行动画的tick
        blockEntity.getData(CommonAttachments.ANIMTICKER).timers.forEach { (_, timer) -> timer.tick() }
    }

}