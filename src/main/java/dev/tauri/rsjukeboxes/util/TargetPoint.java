package dev.tauri.rsjukeboxes.util;

import net.minecraft.server.world.ServerWorld;
import org.jetbrains.annotations.NotNull;

public record TargetPoint(int x, int y, int z, int radius, @NotNull ServerWorld dim) {
}
