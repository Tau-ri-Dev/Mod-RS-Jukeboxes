package dev.tauri.rsjukeboxes.block;

import dev.tauri.rsjukeboxes.blockentity.AbstractRSJukeboxBE;
import dev.tauri.rsjukeboxes.blockentity.AbstractTieredJukeboxBE;
import dev.tauri.rsjukeboxes.blockentity.RepeatingJukeboxBE;
import dev.tauri.rsjukeboxes.screen.container.TieredJukeboxContainer;
import net.fabricmc.fabric.api.screenhandler.v1.ExtendedScreenHandlerFactory;
import net.minecraft.block.BlockState;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.network.PacketByteBuf;
import net.minecraft.screen.ScreenHandler;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.text.Text;
import net.minecraft.util.ActionResult;
import net.minecraft.util.Hand;
import net.minecraft.util.hit.BlockHitResult;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.world.BlockView;
import net.minecraft.world.World;

import java.util.Map;

public abstract class AbstractTieredJukeboxBlock extends AbstractRSJukebox {
    @Override
    public ActionResult onUse(BlockState state, World pLevel, BlockPos pPos, PlayerEntity pPlayer, Hand pHand, BlockHitResult hit) {
        if (!pLevel.isClient) {
            BlockEntity blockentity = pLevel.getBlockEntity(pPos);
            if (blockentity instanceof AbstractTieredJukeboxBE jukebox) {
                if (pPlayer instanceof ServerPlayerEntity sp) {
                    sp.openHandledScreen(new ExtendedScreenHandlerFactory() {
                        @Override
                        public void writeScreenOpeningData(ServerPlayerEntity player, PacketByteBuf buf) {
                            buf.writeBlockPos(jukebox.getPos());
                        }

                        @Override
                        public Text getDisplayName() {
                            return Text.empty();
                        }

                        @Override
                        public ScreenHandler createMenu(int syncId, PlayerInventory playerInventory, PlayerEntity player) {
                            return new TieredJukeboxContainer(syncId, playerInventory, jukebox);
                        }
                    });
                }
            }
        }

        return ActionResult.PASS;
    }

    @Override
    public void processInputSignal(BlockState state, BlockView level, BlockPos pos, BlockPos changedPos, Map<Direction, Integer> signals, AbstractRSJukeboxBE jukeboxBE) {
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
    public int getOutputSignal(BlockState state, BlockView level, BlockPos pos, Direction direction, AbstractRSJukeboxBE jukeboxBE) {
        if (direction.getOpposite() != Direction.SOUTH) return 0;
        if (jukeboxBE.getWorld() == null) return 0;
        if (!jukeboxBE.isPlaying() && (jukeboxBE.getWorld().getTime() - jukeboxBE.playingStopped) <= RepeatingJukeboxBE.STOP_REDSTONE_LENGTH) {
            return 15;
        }
        return 0;
    }
}
