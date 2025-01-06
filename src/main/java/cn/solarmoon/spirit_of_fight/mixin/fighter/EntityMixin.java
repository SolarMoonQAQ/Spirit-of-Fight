package cn.solarmoon.spirit_of_fight.mixin.fighter;

import cn.solarmoon.spirit_of_fight.fighter.EntityPatch;
import cn.solarmoon.spirit_of_fight.fighter.IEntityPatchHolder;
import cn.solarmoon.spirit_of_fight.fighter.player.PlayerPatch;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(Entity.class)
public class EntityMixin implements IEntityPatchHolder {

    private final Entity entity = (Entity) (Object) this;
    private EntityPatch patch = new EntityPatch(entity);

    @Override
    public EntityPatch getPatch() {
        return patch;
    }

    @Override
    public void setPatch(EntityPatch patch) {
        this.patch = patch;
    }
}
