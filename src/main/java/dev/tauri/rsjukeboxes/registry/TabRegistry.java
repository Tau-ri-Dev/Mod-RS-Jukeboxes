package dev.tauri.rsjukeboxes.registry;

import dev.tauri.rsjukeboxes.util.TabHelper;
import net.minecraft.core.registries.Registries;
import net.minecraft.world.item.CreativeModeTab;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.RegistryObject;

import static dev.tauri.rsjukeboxes.RSJukeboxes.MOD_ID;

@SuppressWarnings("unused")
public class TabRegistry {
    public static final DeferredRegister<CreativeModeTab> REGISTER = DeferredRegister.create(Registries.CREATIVE_MODE_TAB, MOD_ID);

    public static final RegistryObject<CreativeModeTab> TAB_JUKEBOXES = TabHelper.createCreativeTab("jukeboxes", () -> BlockRegistry.RS_JUKEBOX_BLOCK);


    public static void register(IEventBus bus) {
        REGISTER.register(bus);
    }
}
