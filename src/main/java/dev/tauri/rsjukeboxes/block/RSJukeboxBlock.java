package dev.tauri.rsjukeboxes.block;

import dev.tauri.rsjukeboxes.blockentity.AbstractRSJukeboxBE;
import dev.tauri.rsjukeboxes.blockentity.RSJukeboxBE;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;

import javax.annotation.ParametersAreNonnullByDefault;

public class RSJukeboxBlock extends AbstractRSJukebox {
    @Override
    public BlockEntity createBlockEntity(BlockPos pPos, BlockState pState) {
        return new RSJukeboxBE(pPos, pState);
    }

    @Override
    @ParametersAreNonnullByDefault
    public void neighborChanged(BlockState pState, Level pLevel, BlockPos pPos, Block pBlock, BlockPos pFromPos, boolean pIsMoving) {
        super.neighborChanged(pState, pLevel, pPos, pBlock, pFromPos, pIsMoving);
        var be = pLevel.getBlockEntity(pPos);
        if (!(be instanceof AbstractRSJukeboxBE jukeboxBE)) return;
        for (var direction : Direction.values()) {
            var signal = pLevel.getSignal(pPos.offset(direction.getNormal()), direction);
            if (signal > 0) {
                jukeboxBE.startPlaying();
                return;
            }
        }
    }
}
