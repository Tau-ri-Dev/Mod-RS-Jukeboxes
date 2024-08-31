package dev.tauri.rsjukeboxes.block;

import dev.tauri.rsjukeboxes.blockentity.RepeatingJukeboxBE;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;

import javax.annotation.ParametersAreNonnullByDefault;

@SuppressWarnings("deprecation")
public class RepeatingJukeboxBlock extends AbstractRSJukebox {
    public RepeatingJukeboxBlock() {
        super(Properties.copy(Blocks.JUKEBOX));
    }

    @Override
    public BlockEntity createBlockEntity(BlockPos pPos, BlockState pState) {
        return new RepeatingJukeboxBE(pPos, pState);
    }

    @Override
    @ParametersAreNonnullByDefault
    public void neighborChanged(BlockState pState, Level pLevel, BlockPos pPos, Block pBlock, BlockPos pFromPos, boolean pIsMoving) {
        super.neighborChanged(pState, pLevel, pPos, pBlock, pFromPos, pIsMoving);
        var be = pLevel.getBlockEntity(pPos);
        if (!(be instanceof RepeatingJukeboxBE jukeboxBE)) return;
        for (var direction : Direction.values()) {
            var signal = pLevel.getSignal(pPos.offset(direction.getNormal()), direction);
            switch (direction) {
                case NORTH:
                    jukeboxBE.setPowered(signal > 0);
                    break;
                case EAST:
                    jukeboxBE.setTickDelayAddition(signal);
                    break;
                case WEST:
                    jukeboxBE.setTickDelayCoef(signal);
                    break;
                default:
                    break;
            }
        }
    }


    @Override
    @ParametersAreNonnullByDefault
    public int getSignal(BlockState pState, BlockGetter pLevel, BlockPos pPos, Direction pDirection) {
        if (pDirection.getOpposite() != Direction.SOUTH) return 0;
        BlockEntity blockentity = pLevel.getBlockEntity(pPos);
        if (blockentity instanceof RepeatingJukeboxBE jukebox) {
            if (jukebox.getLevel() == null) return 0;
            if (!jukebox.isPlaying() && (jukebox.getLevel().getGameTime() - jukebox.playingStopped) <= RepeatingJukeboxBE.STOP_REDSTONE_LENGTH) {
                return 15;
            }
        }
        return 0;
    }
}
