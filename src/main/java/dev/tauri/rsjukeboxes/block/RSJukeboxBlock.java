package dev.tauri.rsjukeboxes.block;

import dev.tauri.rsjukeboxes.blockentity.AbstractRSJukeboxBE;
import dev.tauri.rsjukeboxes.blockentity.RSJukeboxBE;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;

import javax.annotation.ParametersAreNonnullByDefault;
import java.util.Map;

public class RSJukeboxBlock extends AbstractRSJukebox {
    @Override
    public BlockEntity createBlockEntity(BlockPos pPos, BlockState pState) {
        return new RSJukeboxBE(pPos, pState);
    }

    @Override
    @ParametersAreNonnullByDefault
    public void processInputSignal(BlockState state, BlockGetter level, BlockPos pos, BlockPos changedPos, Map<Direction, Integer> signals, AbstractRSJukeboxBE jukeboxBE) {
        for (var signal : signals.values()) {
            if (signal > 0) {
                jukeboxBE.startPlaying();
                return;
            }
        }
    }
}
