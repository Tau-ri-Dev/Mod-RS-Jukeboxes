package dev.tauri.rsjukeboxes.block;

import dev.tauri.rsjukeboxes.blockentity.Tier3JukeboxBE;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;

public class Tier3JukeboxBlock extends AbstractTieredJukeboxBlock {
    @Override
    public BlockEntity createBlockEntity(BlockPos pPos, BlockState pState) {
        return new Tier3JukeboxBE(pPos, pState);
    }
}
