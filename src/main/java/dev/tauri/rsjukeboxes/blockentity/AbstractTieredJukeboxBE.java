package dev.tauri.rsjukeboxes.blockentity;

import dev.tauri.rsjukeboxes.integration.ComputerDeviceHolder;
import dev.tauri.rsjukeboxes.integration.ComputerDeviceProvider;
import dev.tauri.rsjukeboxes.util.PlaySlotSelectResult;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.common.util.LazyOptional;
import org.jetbrains.annotations.NotNull;

import javax.annotation.Nonnull;
import javax.annotation.ParametersAreNonnullByDefault;

public abstract class AbstractTieredJukeboxBE extends AbstractRSJukeboxBE implements ComputerDeviceProvider {
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
        getDeviceHolder().sendSignal("jukebox_playing_stop", "Stopped playing disc");
        super.stopPlaying();
    }

    @Override
    public void startPlaying() {
        getDeviceHolder().sendSignal("jukebox_playing_start", "Started playing disc");
        super.startPlaying();
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
            getDeviceHolder().sendSignal("jukebox_slot_switch", slot, "Switched slot to " + slot);
            break;
        }
        setChanged();
        sendUpdate();
    }

    public PlaySlotSelectResult setSlotToPlay(int slot) {
        if (level == null || level.isClientSide) return PlaySlotSelectResult.CLIENT;
        if (slot >= getContainerSize()) return PlaySlotSelectResult.OUT_OF_BOUNDS;
        if (itemStackHandler.getStackInSlot(slot).isEmpty()) return PlaySlotSelectResult.NO_DISC;
        stopPlayingAndDoNotSkip();
        if (slot == currentSlotPlaying) {
            startPlaying();
            return PlaySlotSelectResult.OK;
        }
        this.currentSlotPlaying = slot;
        getDeviceHolder().sendSignal("jukebox_slot_switch", slot, "Switched slot to " + slot);
        setChanged();
        sendUpdate();
        return PlaySlotSelectResult.OK;
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
    public @Nonnull <T> LazyOptional<T> getCapability(@NotNull Capability<T> capability, Direction facing) {
        var computerCaps = getDeviceHolder().getOrCreateDeviceBasedOnCap(capability);
        if (computerCaps.isPresent())
            return computerCaps;
        return super.getCapability(capability, facing);
    }

    // ----------------------------------------------
    // OC/CC

    public ComputerDeviceHolder computerDeviceHolder;

    public void createDeviceHolder() {
        computerDeviceHolder = new ComputerDeviceHolder(this);
    }

    @Override
    public ComputerDeviceHolder getDeviceHolder() {
        if (computerDeviceHolder == null) createDeviceHolder();
        return computerDeviceHolder;
    }

    @Override
    public String getDeviceType() {
        return "JUKEBOX";
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
