package dev.tauri.rsjukeboxes.block;

import dev.tauri.rsjukeboxes.blockentity.Tier2JukeboxBE;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;

public class Tier2JukeboxBlock extends AbstractTieredJukeboxBlock {
    @Override
    public BlockEntity createBlockEntity(BlockPos pPos, BlockState pState) {
        return new Tier2JukeboxBE(pPos, pState);
    }
}
