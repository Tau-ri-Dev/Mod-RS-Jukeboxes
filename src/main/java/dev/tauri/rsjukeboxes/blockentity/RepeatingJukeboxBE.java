package dev.tauri.rsjukeboxes.blockentity;

import dev.tauri.rsjukeboxes.registry.BlockEntityRegistry;
import net.minecraft.block.BlockState;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.util.math.BlockPos;

public class RepeatingJukeboxBE extends AbstractRSJukeboxBE {
    protected boolean isPowered = false;
    protected int tickDelayCoef = 0;
    protected int tickDelayAddition = 0;

    @SuppressWarnings("unused")
    public boolean isPowered() {
        return isPowered;
    }

    public void setPowered(boolean powered) {
        this.isPowered = powered;
        setChanged();
    }

    public void setTickDelayCoef(int value) {
        this.tickDelayCoef = value;
        setChanged();
    }

    public void setTickDelayAddition(int value) {
        this.tickDelayAddition = value;
        setChanged();
    }

    public RepeatingJukeboxBE(BlockPos pPos, BlockState pBlockState) {
        super(BlockEntityRegistry.REPEATING_JUKEBOX, pPos, pBlockState);
    }

    @Override
    public long getDelayBetweenRecords() {
        return (16L * tickDelayCoef) + tickDelayAddition;
    }

    public static final int STOP_REDSTONE_LENGTH = 4; //ticks

    @Override
    public void tick() {
        super.tick();
        if (world == null || world.isClient) return;
        if (!isPlaying() && isPowered && hasPlayableItem) {
            startPlaying();
        }
        if (isPlaying() && !isPowered) {
            stopPlaying();
        }
        if(world.getTime() - playingStopped == (STOP_REDSTONE_LENGTH + 1)){
            this.world.updateNeighborsAlways(this.getPos(), this.getCachedState().getBlock());
        }
    }

    @Override
    public void readNbt(NbtCompound compound) {
        super.readNbt(compound);
        isPowered = compound.getBoolean("isPowered");
        tickDelayCoef = compound.getInt("tickDelayCoef");
        tickDelayAddition = compound.getInt("tickDelayAddition");
    }

    @Override
    protected void writeNbt(NbtCompound compound) {
        super.writeNbt(compound);
        compound.putBoolean("isPowered", isPowered);
        compound.putInt("tickDelayCoef", tickDelayCoef);
        compound.putInt("tickDelayAddition", tickDelayAddition);
    }
}
