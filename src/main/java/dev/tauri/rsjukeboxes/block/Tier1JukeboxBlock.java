package dev.tauri.rsjukeboxes.block;

import dev.tauri.rsjukeboxes.blockentity.Tier1JukeboxBE;
import net.minecraft.block.BlockState;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.util.math.BlockPos;

public class Tier1JukeboxBlock extends AbstractTieredJukeboxBlock {
    @Override
    public BlockEntity newBlockEntity(BlockPos pPos, BlockState pState) {
        return new Tier1JukeboxBE(pPos, pState);
    }
}
