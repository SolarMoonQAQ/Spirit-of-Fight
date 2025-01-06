package cn.solarmoon.spirit_of_fight.mixin.fighter;

import cn.solarmoon.spirit_of_fight.fighter.IEntityPatchHolder;
import cn.solarmoon.spirit_of_fight.fighter.player.PlayerPatch;
import com.mojang.authlib.GameProfile;
import net.minecraft.core.BlockPos;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(Player.class)
public class PlayerMixin {

    private final Player player = (Player) (Object) this;
    private final PlayerPatch patch = new PlayerPatch(player);

    @Inject(method = "<init>", at = @At("RETURN"))
    private void init(Level level, BlockPos pos, float yRot, GameProfile gameProfile, CallbackInfo ci) {
        ((IEntityPatchHolder)player).setPatch(patch);
    }

}
