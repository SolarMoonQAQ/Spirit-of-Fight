package cn.solarmoon.spark_core.mixin.animation.humanoid.first_person;

import cn.solarmoon.spark_core.api.animation.vanilla.PlayerAnimHelper;
import com.mojang.blaze3d.vertex.PoseStack;
import net.minecraft.client.model.PlayerModel;
import net.minecraft.client.player.AbstractClientPlayer;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.client.renderer.entity.LivingEntityRenderer;
import net.minecraft.client.renderer.entity.player.PlayerRenderer;
import net.minecraft.world.entity.HumanoidArm;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(PlayerRenderer.class)
public abstract class PlayerRendererMixin extends LivingEntityRenderer<AbstractClientPlayer, PlayerModel<AbstractClientPlayer>> {

    public PlayerRendererMixin(EntityRendererProvider.Context context, PlayerModel<AbstractClientPlayer> model, float shadowRadius) {
        super(context, model, shadowRadius);
    }

    @Inject(method = "render(Lnet/minecraft/client/player/AbstractClientPlayer;FFLcom/mojang/blaze3d/vertex/PoseStack;Lnet/minecraft/client/renderer/MultiBufferSource;I)V",
            at = @At(value = "INVOKE", target = "Lnet/minecraft/client/renderer/entity/LivingEntityRenderer;render(Lnet/minecraft/world/entity/LivingEntity;FFLcom/mojang/blaze3d/vertex/PoseStack;Lnet/minecraft/client/renderer/MultiBufferSource;I)V"))
    private void hideUnnecessaryBonesWhenRenderInFirstPerson(AbstractClientPlayer entity, float entityYaw, float partialTicks, PoseStack poseStack, MultiBufferSource buffer, int packedLight, CallbackInfo ci) {
        if (PlayerAnimHelper.shouldRenderArmAnimInFirstPerson(entity)) {
            model.setAllVisible(false);
            var isPlayingMeleeAttack = false; // 是否正在进行近战攻击，否则将只渲染武器
            var showRightArm = PlayerAnimHelper.isPlayingAnimOnArm(entity, HumanoidArm.RIGHT) && isPlayingMeleeAttack;
            var showLeftArm = PlayerAnimHelper.isPlayingAnimOnArm(entity, HumanoidArm.LEFT) && isPlayingMeleeAttack;
            model.rightArm.visible = showRightArm;
            model.rightSleeve.visible = showRightArm;
            model.leftArm.visible = showLeftArm;
            model.leftSleeve.visible = showLeftArm;
        }
    }

}
