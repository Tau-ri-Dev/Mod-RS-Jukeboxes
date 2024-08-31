package dev.tauri.rsjukeboxes.block;

import dev.tauri.rsjukeboxes.blockentity.Tier1JukeboxBE;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;

public class Tier1JukeboxBlock extends AbstractTieredJukeboxBlock {
    @Override
    public BlockEntity createBlockEntity(BlockPos pPos, BlockState pState) {
        return new Tier1JukeboxBE(pPos, pState);
    }
}
