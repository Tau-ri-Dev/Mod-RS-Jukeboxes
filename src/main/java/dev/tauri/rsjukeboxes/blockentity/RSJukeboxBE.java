package dev.tauri.rsjukeboxes.blockentity;

import dev.tauri.rsjukeboxes.registry.BlockEntityRegistry;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.block.state.BlockState;

public class RSJukeboxBE extends AbstractRSJukeboxBE {
    public RSJukeboxBE(BlockPos pPos, BlockState pBlockState) {
        super(BlockEntityRegistry.RS_JUKEBOX.get(), pPos, pBlockState);
    }
}
