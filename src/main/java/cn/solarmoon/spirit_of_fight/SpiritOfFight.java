package cn.solarmoon.spirit_of_fight;

import cn.solarmoon.spark_core.entry_builder.ObjectRegister;
import cn.solarmoon.spirit_of_fight.registry.client.*;
import cn.solarmoon.spirit_of_fight.registry.common.*;
import cn.solarmoon.spirit_of_fight.registry.common.skill.SOFAxeSkills;
import cn.solarmoon.spirit_of_fight.registry.common.skill.SOFHammerSkills;
import cn.solarmoon.spirit_of_fight.registry.common.skill.SOFSwordSkills;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.fml.ModContainer;
import net.neoforged.fml.common.Mod;
import net.neoforged.fml.loading.FMLEnvironment;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@Mod(SpiritOfFight.MOD_ID)
public class SpiritOfFight {

    public static final String MOD_ID = "spirit_of_fight";
    public static final Logger LOGGER = LoggerFactory.getLogger("战魂");
    public static final ObjectRegister REGISTER = new ObjectRegister(MOD_ID, true);

    public SpiritOfFight(IEventBus modEventBus, ModContainer modContainer) {
        REGISTER.register(modEventBus);

        if (FMLEnvironment.dist.isClient()) {
            SOFClientEventRegister.register();
            SOFKeyMappings.register();
            SOFGuiRegister.register(modEventBus);
            SOFLocalControllerRegister.register(modEventBus);
            SOFItemInHandModelRegister.register(modEventBus);
        }

        SOFRegistries.register();
        SOFItems.register();
        SOFAttachments.register();
        SOFCommonEventRegister.register();
        SOFVisualEffects.register();
        SOFTypedAnimations.register();
        SOFDataGenerater.register(modEventBus);
        SOFPayloadRegister.register(modEventBus);
        SOFSkillControllerRegister.register();
        SOFBodyTypes.register();
        SOFHitTypes.register(modEventBus);

        SOFSwordSkills.register();
        SOFHammerSkills.register();
        SOFAxeSkills.register();
    }


}
