package dev.tauri.rsjukeboxes.registry;

import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.item.ItemStack;

import javax.annotation.Nonnull;

public class TabRegistry {
    public static final CreativeTabs TAB_JUKEBOXES = new CreativeTabs("jukeboxes") {
        @Nonnull
        @Override
        public ItemStack getTabIconItem() {
            return new ItemStack(BlockRegistry.RS_JUKEBOX_BLOCK);
        }
    };
}
