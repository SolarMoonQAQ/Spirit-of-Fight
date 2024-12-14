package cn.solarmoon.spark_core.api.entity.attack

import com.mojang.serialization.Codec
import com.mojang.serialization.codecs.RecordCodecBuilder
import net.minecraft.nbt.CompoundTag
import net.minecraft.util.ExtraCodecs
import net.minecraft.world.level.Level
import org.ode4j.ode.DGeom
import java.util.Optional

/**
 * 受击信息，不包含伤害源和伤害值，如果想调用这两个以检测直接找Entity的hurt方法插入即可
 * @param attacker 发起攻击的实体，具体是箭还是说射箭的生物，需要看自己的攻击逻辑而定
 * @param damageBox 触发该次攻击的box，可以通过box大小位置等信息实现想要的效果
 * @param damageBone 该次攻击的box所击中的骨骼
 * @param extraData 可以附加额外数据到此次攻击
 */
data class AttackedData(
    val attacker: Int?,
    val damageBox: DGeom,
    val damageBone: String?,
    val extraData: CompoundTag = CompoundTag()
) {



}