package dev.tauri.rsjukeboxes.block;

import dev.tauri.rsjukeboxes.blockentity.Tier3JukeboxBE;
import net.minecraft.block.BlockState;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.util.math.BlockPos;

public class Tier3JukeboxBlock extends AbstractTieredJukeboxBlock {
    @Override
    public BlockEntity newBlockEntity(BlockPos pPos, BlockState pState) {
        return new Tier3JukeboxBE(pPos, pState);
    }
}
