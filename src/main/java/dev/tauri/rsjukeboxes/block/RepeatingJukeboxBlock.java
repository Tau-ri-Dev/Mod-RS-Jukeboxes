package dev.tauri.rsjukeboxes.block;

import dev.tauri.rsjukeboxes.blockentity.AbstractRSJukeboxBE;
import dev.tauri.rsjukeboxes.blockentity.RepeatingJukeboxBE;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;

import javax.annotation.ParametersAreNonnullByDefault;
import java.util.Map;

public class RepeatingJukeboxBlock extends AbstractRSJukebox {
    @Override
    public BlockEntity createBlockEntity(BlockPos pPos, BlockState pState) {
        return new RepeatingJukeboxBE(pPos, pState);
    }

    @Override
    @ParametersAreNonnullByDefault
    public void processInputSignal(BlockState state, BlockGetter level, BlockPos pos, BlockPos changedPos, Map<Direction, Integer> signals, AbstractRSJukeboxBE jukeboxBE) {
        if (!(jukeboxBE instanceof RepeatingJukeboxBE repeatingJukeboxBE)) return;
        for (var e : signals.entrySet()) {
            var direction = e.getKey();
            var signal = e.getValue();
            switch (direction) {
                case NORTH:
                    repeatingJukeboxBE.setPowered(signal > 0);
                    break;
                case EAST:
                    repeatingJukeboxBE.setTickDelayAddition(signal);
                    break;
                case WEST:
                    repeatingJukeboxBE.setTickDelayCoef(signal);
                    break;
                default:
                    break;
            }
        }
    }


    @Override
    @ParametersAreNonnullByDefault
    public int getOutputSignal(BlockState state, BlockGetter level, BlockPos pos, Direction direction, AbstractRSJukeboxBE jukeboxBE) {
        if (direction.getOpposite() != Direction.SOUTH) return 0;
        BlockEntity blockentity = level.getBlockEntity(pos);
        if (blockentity instanceof RepeatingJukeboxBE jukebox) {
            if (jukebox.getLevel() == null) return 0;
            if (!jukebox.isPlaying() && (jukebox.getLevel().getGameTime() - jukebox.playingStopped) <= RepeatingJukeboxBE.STOP_REDSTONE_LENGTH) {
                return 15;
            }
        }
        return 0;
    }
}
