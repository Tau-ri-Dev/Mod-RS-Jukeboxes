package dev.tauri.rsjukeboxes.block;

import dev.tauri.rsjukeboxes.blockentity.AbstractRSJukeboxBE;
import dev.tauri.rsjukeboxes.item.ITabbedItem;
import dev.tauri.rsjukeboxes.registry.TabRegistry;
import dev.tauri.rsjukeboxes.util.ITickable;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.stats.Stats;
import net.minecraft.tags.ItemTags;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.CreativeModeTab;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.RecordItem;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.JukeboxBlock;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityTicker;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraftforge.registries.RegistryObject;
import org.jetbrains.annotations.NotNull;

import javax.annotation.Nullable;
import javax.annotation.ParametersAreNonnullByDefault;

public abstract class AbstractRSJukebox extends JukeboxBlock implements ITabbedItem {
    public AbstractRSJukebox(Properties properties) {
        super(properties);
    }

    @Override
    public RegistryObject<CreativeModeTab> getTab() {
        return TabRegistry.TAB_JUKEBOXES;
    }

    @ParametersAreNonnullByDefault
    public void setPlacedBy(Level pLevel, BlockPos pPos, BlockState pState, @Nullable LivingEntity pPlacer, ItemStack pStack) {
        super.setPlacedBy(pLevel, pPos, pState, pPlacer, pStack);
    }

    @ParametersAreNonnullByDefault
    public @NotNull InteractionResult use(BlockState pState, Level pLevel, BlockPos pPos, Player pPlayer, InteractionHand pHand, BlockHitResult pHit) {
        if (!pLevel.isClientSide) {
            BlockEntity blockentity = pLevel.getBlockEntity(pPos);
            if (blockentity instanceof AbstractRSJukeboxBE jukebox) {
                var item = pPlayer.getItemInHand(pHand);
                if (pState.getValue(JukeboxBlock.HAS_RECORD)) {
                    jukebox.popOutRecord(0);
                    return InteractionResult.sidedSuccess(false);
                }
                if (item.is(ItemTags.MUSIC_DISCS) && !item.isEmpty()) {
                    jukebox.itemStackHandler.setStackInSlot(0, item.copy());
                    item.shrink(1);
                    pPlayer.awardStat(Stats.PLAY_RECORD);
                    return InteractionResult.sidedSuccess(false);
                }
            }
        }

        return InteractionResult.PASS;
    }

    @ParametersAreNonnullByDefault
    public void onRemove(BlockState pState, Level pLevel, BlockPos pPos, BlockState pNewState, boolean pIsMoving) {
        if (!pState.is(pNewState.getBlock())) {
            BlockEntity blockentity = pLevel.getBlockEntity(pPos);
            if (blockentity instanceof AbstractRSJukeboxBE jukebox) {
                jukebox.popOutRecords();
            }

            super.onRemove(pState, pLevel, pPos, pNewState, pIsMoving);
        }
    }

    @ParametersAreNonnullByDefault
    public boolean isSignalSource(BlockState pState) {
        return true;
    }

    @ParametersAreNonnullByDefault
    public int getSignal(BlockState pState, BlockGetter pLevel, BlockPos pPos, Direction pDirection) {
        BlockEntity blockentity = pLevel.getBlockEntity(pPos);
        if (blockentity instanceof AbstractRSJukeboxBE jukebox) {
            if (jukebox.isPlaying()) {
                return 15;
            }
        }

        return 0;
    }

    @ParametersAreNonnullByDefault
    public boolean hasAnalogOutputSignal(BlockState pState) {
        return true;
    }

    @ParametersAreNonnullByDefault
    public int getAnalogOutputSignal(BlockState pBlockState, Level pLevel, BlockPos pPos) {
        BlockEntity blockentity = pLevel.getBlockEntity(pPos);
        if (blockentity instanceof AbstractRSJukeboxBE jukebox) {
            Item item = jukebox.getFirstItem().getItem();
            if (item instanceof RecordItem recorditem) {
                return recorditem.getAnalogOutput();
            }
        }

        return 0;
    }

    @Override
    @ParametersAreNonnullByDefault
    public BlockEntity newBlockEntity(BlockPos pPos, BlockState pState) {
        return createBlockEntity(pPos, pState);
    }

    public abstract BlockEntity createBlockEntity(BlockPos pPos, BlockState pState);

    @Nullable
    @Override
    @ParametersAreNonnullByDefault
    public <T extends BlockEntity> BlockEntityTicker<T> getTicker(Level pLevel, BlockState pState, BlockEntityType<T> pBlockEntityType) {
        return ITickable.getTickerHelper();
    }
}
