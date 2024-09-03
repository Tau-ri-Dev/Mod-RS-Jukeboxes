package dev.tauri.rsjukeboxes.registry;

import dev.tauri.rsjukeboxes.util.TabHelper;
import net.minecraft.item.ItemGroup;
import net.minecraft.item.ItemStack;

import java.util.function.Supplier;

public class TabRegistry {
    public static final Supplier<ItemGroup> TAB_JUKEBOXES = TabHelper.createCreativeTab("jukeboxes", () -> new ItemStack(BlockRegistry.RS_JUKEBOX_BLOCK));

    public static void register() {

    }
}
