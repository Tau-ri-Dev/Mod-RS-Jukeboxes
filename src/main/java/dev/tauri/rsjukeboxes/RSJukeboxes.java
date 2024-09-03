package dev.tauri.rsjukeboxes;

import dev.tauri.rsjukeboxes.packet.RSJPacketHandler;
import dev.tauri.rsjukeboxes.registry.*;
import net.fabricmc.api.ModInitializer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class RSJukeboxes implements ModInitializer {
    public static final String MOD_ID = "rsjukeboxes";
    public static final String MOD_NAME = "Redstone Jukeboxes";
    public static Logger logger;

    @Override
    public void onInitialize() {
        logger = LoggerFactory.getLogger(MOD_NAME);
        TabRegistry.register();
        BlockRegistry.register();
        ItemRegistry.register();
        BlockEntityRegistry.register();
        MenuTypeRegistry.register();

        RSJPacketHandler.init();
    }
}
