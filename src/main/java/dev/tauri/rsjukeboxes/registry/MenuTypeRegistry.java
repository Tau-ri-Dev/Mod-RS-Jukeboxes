package dev.tauri.rsjukeboxes.registry;

import dev.tauri.rsjukeboxes.screen.container.TieredJukeboxContainer;
import net.minecraft.world.inventory.MenuType;
import net.minecraftforge.common.extensions.IForgeMenuType;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.RegistryObject;

import static dev.tauri.rsjukeboxes.RSJukeboxes.MOD_ID;

public class MenuTypeRegistry {
    public static final DeferredRegister<MenuType<?>> REGISTER = DeferredRegister.create(ForgeRegistries.MENU_TYPES, MOD_ID);

    public static final RegistryObject<MenuType<TieredJukeboxContainer>> TIERED_JUKEBOX_MENU_TYPE = REGISTER.register("tiered_jukebox",
            () -> IForgeMenuType.create(TieredJukeboxContainer::new)
    );

    public static void register(IEventBus bus) {
        REGISTER.register(bus);
    }

}
