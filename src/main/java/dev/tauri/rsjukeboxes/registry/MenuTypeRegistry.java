package dev.tauri.rsjukeboxes.registry;

import dev.tauri.rsjukeboxes.screen.container.TieredJukeboxContainer;
import net.fabricmc.fabric.api.screenhandler.v1.ExtendedScreenHandlerType;
import net.minecraft.registry.Registries;
import net.minecraft.registry.Registry;
import net.minecraft.screen.ScreenHandler;
import net.minecraft.screen.ScreenHandlerType;
import net.minecraft.util.Identifier;

import static dev.tauri.rsjukeboxes.RSJukeboxes.MOD_ID;

public class MenuTypeRegistry {

    public static final ScreenHandlerType<TieredJukeboxContainer> TIERED_JUKEBOX_MENU_TYPE = registerMenuType("tiered_jukebox", TieredJukeboxContainer::new);

    public static <T extends ScreenHandler> ScreenHandlerType<T> registerMenuType(String name, ExtendedScreenHandlerType.ExtendedFactory<T> factory) {
        return Registry.register(Registries.SCREEN_HANDLER, new Identifier(MOD_ID, name), new ExtendedScreenHandlerType<>(factory));
    }

    public static void register() {
    }

}
