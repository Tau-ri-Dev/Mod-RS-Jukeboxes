package dev.tauri.rsjukeboxes.block;

import dev.tauri.rsjukeboxes.blockentity.AbstractRSJukeboxBE;
import dev.tauri.rsjukeboxes.blockentity.RepeatingJukeboxBE;
import net.minecraft.block.BlockState;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.world.BlockView;

import java.util.Map;

public class RepeatingJukeboxBlock extends AbstractRSJukebox {
    @Override
    public BlockEntity newBlockEntity(BlockPos pPos, BlockState pState) {
        return new RepeatingJukeboxBE(pPos, pState);
    }

    @Override
    public void processInputSignal(BlockState state, BlockView level, BlockPos pos, BlockPos changedPos, Map<Direction, Integer> signals, AbstractRSJukeboxBE jukeboxBE) {
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
    public int getOutputSignal(BlockState state, BlockView level, BlockPos pos, Direction direction, AbstractRSJukeboxBE jukeboxBE) {
        if (direction.getOpposite() != Direction.SOUTH) return 0;
        BlockEntity blockentity = level.getBlockEntity(pos);
        if (blockentity instanceof RepeatingJukeboxBE jukebox) {
            if (jukebox.getWorld() == null) return 0;
            if (!jukebox.isPlaying() && (jukebox.getWorld().getTime() - jukebox.playingStopped) <= RepeatingJukeboxBE.STOP_REDSTONE_LENGTH) {
                return 15;
            }
        }
        return 0;
    }
}
