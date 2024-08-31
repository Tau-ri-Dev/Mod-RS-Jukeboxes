package dev.tauri.rsjukeboxes;

import dev.tauri.rsjukeboxes.packet.RSJPacketHandler;
import dev.tauri.rsjukeboxes.registry.*;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.ModList;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.event.lifecycle.FMLClientSetupEvent;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@Mod(RSJukeboxes.MOD_ID)
public class RSJukeboxes {
    public static final String MOD_ID = "rsjukeboxes";
    public static final String MOD_NAME = "Redstone Jukeboxes";
    public static String MOD_VERSION = "";
    public static final String MC_VERSION = "1.20.1";
    public static Logger logger;

    public RSJukeboxes() {
        logger = LoggerFactory.getLogger(MOD_NAME);

        ModList.get().getModContainerById(MOD_ID).ifPresentOrElse(container -> {
            MOD_VERSION = MC_VERSION + "-" + container.getModInfo().getVersion().getQualifier();
        }, () -> {
        });
        logger.info("Loading RSJukeboxes version " + MOD_VERSION);


        IEventBus modEventBus = FMLJavaModLoadingContext.get().getModEventBus();

        ItemRegistry.register(modEventBus);
        BlockRegistry.register(modEventBus);
        TabRegistry.register(modEventBus);
        BlockEntityRegistry.register(modEventBus);

        MinecraftForge.EVENT_BUS.register(this);
        modEventBus.addListener(BlockEntityRegistry::registerBERs);
        RSJPacketHandler.init();
    }

    @Mod.EventBusSubscriber(modid = MOD_ID, bus = Mod.EventBusSubscriber.Bus.MOD, value = Dist.CLIENT)
    public static class ClientModEvents {
        @SubscribeEvent
        public static void onClientSetup(FMLClientSetupEvent event) {
        }
    }
}
