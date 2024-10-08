package dev.tauri.rsjukeboxes.block;

import dev.tauri.rsjukeboxes.blockentity.AbstractRSJukeboxBE;
import dev.tauri.rsjukeboxes.blockentity.AbstractTieredJukeboxBE;
import dev.tauri.rsjukeboxes.blockentity.RepeatingJukeboxBE;
import dev.tauri.rsjukeboxes.screen.container.TieredJukeboxContainer;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.SimpleMenuProvider;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraftforge.network.NetworkHooks;
import org.jetbrains.annotations.NotNull;

import javax.annotation.ParametersAreNonnullByDefault;
import java.util.Map;

public abstract class AbstractTieredJukeboxBlock extends AbstractRSJukebox {
    public AbstractTieredJukeboxBlock() {
        super(Properties.copy(Blocks.JUKEBOX));
    }

    @ParametersAreNonnullByDefault
    public @NotNull InteractionResult use(BlockState pState, Level pLevel, BlockPos pPos, Player pPlayer, InteractionHand pHand, BlockHitResult pHit) {
        if (!pLevel.isClientSide) {
            BlockEntity blockentity = pLevel.getBlockEntity(pPos);
            if (blockentity instanceof AbstractTieredJukeboxBE jukebox) {
                if (pPlayer instanceof ServerPlayer sp) {
                    NetworkHooks.openScreen(sp, new SimpleMenuProvider((id, pInv, p) -> new TieredJukeboxContainer(id, pInv, jukebox), Component.empty()), jukebox.getBlockPos());
                }
            }
        }

        return InteractionResult.PASS;
    }

    @Override
    @ParametersAreNonnullByDefault
    public void processInputSignal(BlockState state, BlockGetter level, BlockPos pos, BlockPos changedPos, Map<Direction, Integer> signals, AbstractRSJukeboxBE jukeboxBE) {
        if (!(jukeboxBE instanceof AbstractTieredJukeboxBE tieredJukeboxBE)) return;
        for (var e : signals.entrySet()) {
            var direction = e.getKey();
            var signal = e.getValue();
            switch (direction) {
                case NORTH:
                    tieredJukeboxBE.setPowered(signal > 0);
                    break;
                case EAST:
                    if (signal > 0) {
                        tieredJukeboxBE.selectPreviousTrack();
                    }
                    break;
                case WEST:
                    if (signal > 0) {
                        tieredJukeboxBE.selectNextTrack();
                    }
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
        if (jukeboxBE.getLevel() == null) return 0;
        if (!jukeboxBE.isPlaying() && (jukeboxBE.getLevel().getGameTime() - jukeboxBE.playingStopped) <= RepeatingJukeboxBE.STOP_REDSTONE_LENGTH) {
            return 15;
        }
        return 0;
    }
}
