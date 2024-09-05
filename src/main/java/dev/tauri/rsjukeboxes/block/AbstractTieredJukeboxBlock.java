package dev.tauri.rsjukeboxes.block;

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
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.BlockHitResult;
import org.jetbrains.annotations.NotNull;

import javax.annotation.ParametersAreNonnullByDefault;

public abstract class AbstractTieredJukeboxBlock extends AbstractRSJukebox {
    @ParametersAreNonnullByDefault
    public @NotNull InteractionResult use(BlockState pState, Level pLevel, BlockPos pPos, Player pPlayer, InteractionHand pHand, BlockHitResult pHit) {
        if (!pLevel.isClientSide) {
            BlockEntity blockentity = pLevel.getBlockEntity(pPos);
            if (blockentity instanceof AbstractTieredJukeboxBE jukebox) {
                if (pPlayer instanceof ServerPlayer sp) {
                    sp.openMenu(new SimpleMenuProvider((id, pInv, p) -> new TieredJukeboxContainer(id, pInv, jukebox), Component.empty()), jukebox.getBlockPos());
                }
            }
        }

        return InteractionResult.PASS;
    }

    @Override
    @ParametersAreNonnullByDefault
    public void neighborChanged(BlockState pState, Level pLevel, BlockPos pPos, Block pBlock, BlockPos pFromPos, boolean pIsMoving) {
        super.neighborChanged(pState, pLevel, pPos, pBlock, pFromPos, pIsMoving);
        if (pLevel.isClientSide) return;
        if (pIsMoving) return;
        var be = pLevel.getBlockEntity(pPos);
        if (!(be instanceof AbstractTieredJukeboxBE jukeboxBE)) return;
        for (var direction : Direction.values()) {
            if (!pPos.offset(direction.getNormal()).equals(pFromPos)) continue;
            var signal = pLevel.getSignal(pFromPos, direction);
            switch (direction) {
                case NORTH:
                    jukeboxBE.setPowered(signal > 0);
                    break;
                case EAST:
                    if (signal > 0) {
                        jukeboxBE.selectPreviousTrack();
                    }
                    break;
                case WEST:
                    if (signal > 0) {
                        jukeboxBE.selectNextTrack();
                    }
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
        if (blockentity instanceof AbstractTieredJukeboxBE jukebox) {
            if (jukebox.getLevel() == null) return 0;
            if (!jukebox.isPlaying() && (jukebox.getLevel().getGameTime() - jukebox.playingStopped) <= RepeatingJukeboxBE.STOP_REDSTONE_LENGTH) {
                return 15;
            }
        }
        return 0;
    }
}
