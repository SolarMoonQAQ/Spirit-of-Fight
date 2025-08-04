package cn.solarmoon.spirit_of_fight.mixin.fighter;

import cn.solarmoon.spark_core.util.MoveDirection;
import cn.solarmoon.spirit_of_fight.entity.IEntityPatch;
import cn.solarmoon.spirit_of_fight.entity.WieldStyle;
import cn.solarmoon.spirit_of_fight.entity.grab.GrabManager;
import cn.solarmoon.spirit_of_fight.poise_system.PoiseData;
import cn.solarmoon.spirit_of_fight.skill.tree.SkillTreeSet;
import net.minecraft.world.entity.Entity;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.spongepowered.asm.mixin.Mixin;

@Mixin(Entity.class)
public class EntityMixin implements IEntityPatch {

    private final Entity entity = (Entity) (Object) this;
    private MoveDirection side;
    private final GrabManager grabManager = new GrabManager((Entity) (Object) this);
    private SkillTreeSet treeSet;
    private boolean hitting = false;
    private boolean knockDown = false;
    private int chargingTime = 0;
    private WieldStyle wieldStyle = WieldStyle.DEFAULT;
    private final PoiseData poise = new PoiseData(entity, 100, 100);

    @Override
    public @Nullable MoveDirection getMoveDirection() {
        return side;
    }

    @Override
    public void setMoveDirection(@Nullable MoveDirection moveDirection) {
        this.side = moveDirection;
    }

    @Override
    public @NotNull GrabManager getGrabManager() {
        return grabManager;
    }

    @Override
    public @Nullable SkillTreeSet getCurrentSkillSet() {
        return treeSet;
    }

    @Override
    public void setCurrentSkillSet(@Nullable SkillTreeSet skillTrees) {
        treeSet = skillTrees;
    }

    @Override
    public boolean isHitting() {
        return hitting;
    }

    @Override
    public void setHitting(boolean b) {
        hitting = b;
    }

    @Override
    public boolean isKnockedDown() {
        return knockDown;
    }

    @Override
    public void setKnockedDown(boolean b) {
        knockDown = b;
    }

    @Override
    public int getChargingTime() {
        return chargingTime;
    }

    @Override
    public void setChargingTime(int i) {
        chargingTime = i;
    }

    @Override
    public @NotNull WieldStyle getWieldStyle() {
        return wieldStyle;
    }

    @Override
    public void setWieldStyle(@NotNull WieldStyle wieldStyle) {
        this.wieldStyle = wieldStyle;
    }

    @Override
    public @NotNull PoiseData getPoise() {
        return poise;
    }

}
