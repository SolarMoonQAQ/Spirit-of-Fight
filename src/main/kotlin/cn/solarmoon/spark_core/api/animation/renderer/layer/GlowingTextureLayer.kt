package cn.solarmoon.spark_core.api.animation.renderer.layer

import cn.solarmoon.spark_core.api.animation.IAnimatable
import cn.solarmoon.spark_core.api.animation.renderer.IGeoRenderer
import com.mojang.blaze3d.vertex.PoseStack
import net.minecraft.client.renderer.MultiBufferSource
import net.minecraft.client.renderer.RenderType
import net.minecraft.client.renderer.texture.OverlayTexture
import net.minecraft.resources.ResourceLocation
import net.minecraft.world.entity.LivingEntity
import net.neoforged.neoforge.attachment.IAttachmentHolder

class GlowingTextureLayer<T: IAttachmentHolder>(renderer: IGeoRenderer<T>): RenderLayer<T>(renderer) {

    override fun getTextureLocation(sth: IAnimatable<T>): ResourceLocation {
        val id = sth.animData.textureLocation
        val path = id.path
        val basePath = path.substringBeforeLast(".")
        val newPath = "${basePath}_glow.png"
        return ResourceLocation.fromNamespaceAndPath(id.namespace, newPath)
    }

    override fun getRenderType(sth: IAnimatable<T>): RenderType {
        return RenderType.eyes(getTextureLocation(sth))
    }

    override fun render(
        sth: IAnimatable<T>,
        partialTick: Float,
        poseStack: PoseStack,
        bufferSource: MultiBufferSource,
        packedLight: Int,
        packedOverlay: Int
    ) {
        val buffer = bufferSource.getBuffer(getRenderType(sth))
        val animData = sth.animData
        val model = animData.model
        poseStack.pushPose()
        val overlay = if (sth is LivingEntity && sth.hurtTime > 0) 0 else OverlayTexture.NO_OVERLAY // 受击变红
        val matrix = sth.getPositionMatrix(partialTick)
        model.renderBones(animData.playData, matrix, sth.getExtraTransform(partialTick), poseStack.last().normal(), buffer, packedLight, overlay, -1, partialTick)
        poseStack.popPose()
    }

}