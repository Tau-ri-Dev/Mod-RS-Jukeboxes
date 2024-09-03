package dev.tauri.rsjukeboxes.item;

import net.minecraft.item.ItemGroup;
import org.jetbrains.annotations.Nullable;

import java.util.function.Supplier;

public interface ITabbedItem {
    @Nullable
    default Supplier<ItemGroup> getTab() {
        return null;
    }
}
