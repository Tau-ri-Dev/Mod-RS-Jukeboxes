package dev.tauri.rsjukeboxes.blockentity;

import dev.tauri.rsjukeboxes.registry.BlockEntityRegistry;
import net.minecraft.block.BlockState;
import net.minecraft.util.math.BlockPos;

public class RSJukeboxBE extends AbstractRSJukeboxBE {
    public RSJukeboxBE(BlockPos pPos, BlockState pBlockState) {
        super(BlockEntityRegistry.RS_JUKEBOX, pPos, pBlockState);
    }
}
