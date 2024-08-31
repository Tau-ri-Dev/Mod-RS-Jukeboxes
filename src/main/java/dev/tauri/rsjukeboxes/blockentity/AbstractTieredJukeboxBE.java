package dev.tauri.rsjukeboxes.blockentity;

import net.minecraft.core.BlockPos;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;

import javax.annotation.ParametersAreNonnullByDefault;

public abstract class AbstractTieredJukeboxBE extends AbstractRSJukeboxBE {
    protected boolean isPowered = false;
    protected boolean lastPowerState = false;

    protected long lastTrackChangeTime;

    @SuppressWarnings("unused")
    public boolean isPowered() {
        return isPowered;
    }

    public void setPowered(boolean powered) {
        this.isPowered = powered;
        setChanged();
    }

    public AbstractTieredJukeboxBE(BlockEntityType<?> type, BlockPos pPos, BlockState pBlockState) {
        super(type, pPos, pBlockState);
    }

    public static final int STOP_REDSTONE_LENGTH = 4; //ticks

    @Override
    public void tick() {
        super.tick();
        if (level == null || level.isClientSide) return;
        if (!hasPlayableItem()) {
            selectFirstPlayableSlot(false);
        }
        if (lastPowerState != isPowered && isPowered) {
            if (isPlaying()) {
                stopPlaying();
            } else if (hasPlayableItem()) {
                startPlaying();
            }
        }
        lastPowerState = isPowered;
        if (level.getGameTime() - playingStopped == (STOP_REDSTONE_LENGTH + 1)) {
            this.level.updateNeighborsAt(this.getBlockPos(), this.getBlockState().getBlock());
        }
    }

    @Override
    public void stopPlaying() {
        stopPlayingAndDoNotSkip();
        if (!isPowered) {
            selectNextTrack();
        }
    }

    public void stopPlayingAndDoNotSkip() {
        super.stopPlaying();
    }

    protected void selectFirstPlayableSlot(boolean previous) {
        if (level == null || level.isClientSide) return;
        stopPlayingAndDoNotSkip();
        var offset = currentSlotPlaying;
        for (int i = offset; (previous ? (i > -getContainerSize() + offset) : (i < getContainerSize() + offset)); i += (previous ? -1 : 1)) {
            var slot = i % getContainerSize();
            while (slot < 0) {
                slot += getContainerSize();
            }
            if (itemStackHandler.getStackInSlot(slot).isEmpty()) continue;
            if (slot == currentSlotPlaying) continue;
            this.currentSlotPlaying = slot;
            break;
        }
        setChanged();
        sendUpdate();
    }

    public void selectNextTrack() {
        if (level == null || level.getGameTime() - lastTrackChangeTime < 2) return;
        lastTrackChangeTime = level.getGameTime();
        selectFirstPlayableSlot(false);
    }

    public void selectPreviousTrack() {
        if (level == null || level.getGameTime() - lastTrackChangeTime < 2) return;
        lastTrackChangeTime = level.getGameTime();
        selectFirstPlayableSlot(true);
    }

    @Override
    @ParametersAreNonnullByDefault
    public void load(CompoundTag compound) {
        super.load(compound);
        isPowered = compound.getBoolean("isPowered");
        lastPowerState = compound.getBoolean("lastPowerState");
    }

    @Override
    @ParametersAreNonnullByDefault
    protected void saveAdditional(CompoundTag compound) {
        super.saveAdditional(compound);
        compound.putBoolean("isPowered", isPowered);
        compound.putBoolean("lastPowerState", lastPowerState);
    }


    public abstract ResourceLocation getGuiBackground();
}
