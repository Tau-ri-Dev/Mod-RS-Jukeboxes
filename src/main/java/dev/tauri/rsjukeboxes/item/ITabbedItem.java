package dev.tauri.rsjukeboxes.item;

import net.minecraft.world.item.CreativeModeTab;
import net.minecraftforge.registries.RegistryObject;
import org.jetbrains.annotations.Nullable;

public interface ITabbedItem {
    @Nullable
    default RegistryObject<CreativeModeTab> getTab() {
        return null;
    }
}
