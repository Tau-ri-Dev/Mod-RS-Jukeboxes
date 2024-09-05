package dev.tauri.rsjukeboxes.util;

import net.minecraft.core.Holder;
import net.minecraft.core.component.DataComponents;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.JukeboxPlayable;
import net.minecraft.world.item.JukeboxSong;
import net.minecraft.world.level.Level;
import org.jetbrains.annotations.Nullable;

public class JukeboxPlayableUtils {
    public static boolean test(ItemStack stack) {
        if (stack == null) return false;
        return getData(stack) != null;
    }

    @Nullable
    public static JukeboxSong getSong(Level level, ItemStack stack) {
        if (level == null) return null;
        var holder = JukeboxSong.fromStack(level.registryAccess(), stack);
        return holder.map(Holder::get).orElse(null);
    }

    @Nullable
    public static JukeboxPlayable getData(ItemStack stack) {
        return stack.get(DataComponents.JUKEBOX_PLAYABLE);
    }
}
