package dev.tauri.rsjukeboxes.block;

import dev.tauri.rsjukeboxes.blockentity.AbstractRSJukeboxBE;
import dev.tauri.rsjukeboxes.blockentity.RSJukeboxBE;
import net.minecraft.block.BlockState;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.world.BlockView;

import java.util.Map;

public class RSJukeboxBlock extends AbstractRSJukebox {
    @Override
    public BlockEntity newBlockEntity(BlockPos pPos, BlockState pState) {
        return new RSJukeboxBE(pPos, pState);
    }

    @Override
    public void processInputSignal(BlockState state, BlockView level, BlockPos pos, BlockPos changedPos, Map<Direction, Integer> signals, AbstractRSJukeboxBE jukeboxBE) {
        for (var signal : signals.values()) {
            if (signal > 0) {
                jukeboxBE.startPlaying();
                return;
            }
        }
    }
}
