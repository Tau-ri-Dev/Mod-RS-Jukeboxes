package dev.tauri.rsjukeboxes.blockentity;

import net.minecraft.block.BlockState;
import net.minecraft.block.entity.BlockEntityType;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.BlockPos;

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
        if (world == null || world.isClient) return;
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
        if (world.getTime() - playingStopped == (STOP_REDSTONE_LENGTH + 1)) {
            this.world.updateNeighborsAlways(this.getPos(), this.getCachedState().getBlock());
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
        if (world == null || world.isClient) return;
        stopPlayingAndDoNotSkip();
        var offset = currentSlotPlaying;
        for (int i = offset; (previous ? (i > -getContainerSize() + offset) : (i < getContainerSize() + offset)); i += (previous ? -1 : 1)) {
            var slot = i % getContainerSize();
            while (slot < 0) {
                slot += getContainerSize();
            }
            if (itemStackHandler.getStack(slot).isEmpty()) continue;
            if (slot == currentSlotPlaying) continue;
            this.currentSlotPlaying = slot;
            break;
        }
        setChanged();
        sendUpdate();
    }

    public void selectNextTrack() {
        if (world == null || world.getTime() - lastTrackChangeTime < 2) return;
        lastTrackChangeTime = world.getTime();
        selectFirstPlayableSlot(false);
    }

    public void selectPreviousTrack() {
        if (world == null || world.getTime() - lastTrackChangeTime < 2) return;
        lastTrackChangeTime = world.getTime();
        selectFirstPlayableSlot(true);
    }

    @Override
    public void readNbt(NbtCompound compound) {
        super.readNbt(compound);
        isPowered = compound.getBoolean("isPowered");
        lastPowerState = compound.getBoolean("lastPowerState");
    }

    @Override
    protected void writeNbt(NbtCompound compound) {
        super.writeNbt(compound);
        compound.putBoolean("isPowered", isPowered);
        compound.putBoolean("lastPowerState", lastPowerState);
    }


    public abstract Identifier getGuiBackground();
}
