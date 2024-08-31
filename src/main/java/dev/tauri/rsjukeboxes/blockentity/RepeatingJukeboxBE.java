package dev.tauri.rsjukeboxes.blockentity;

import dev.tauri.rsjukeboxes.registry.BlockEntityRegistry;
import net.minecraft.core.BlockPos;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.level.block.state.BlockState;

import javax.annotation.ParametersAreNonnullByDefault;

public class RepeatingJukeboxBE extends AbstractRSJukeboxBE {
    protected boolean isPowered = false;
    protected int tickDelayCoef = 0;
    protected int tickDelayAddition = 0;

    public boolean isPowered(){
        return isPowered;
    }

    public void setPowered(boolean powered){
        this.isPowered = powered;
        setChanged();
    }

    public void setTickDelayCoef(int value){
        this.tickDelayCoef = value;
        setChanged();
    }

    public void setTickDelayAddition(int value){
        this.tickDelayAddition = value;
        setChanged();
    }

    public RepeatingJukeboxBE(BlockPos pPos, BlockState pBlockState) {
        super(BlockEntityRegistry.REPEATING_JUKEBOX.get(), pPos, pBlockState);
    }

    @Override
    public long getDelayBetweenRecords(){
        return (16L * tickDelayCoef) + tickDelayAddition;
    }

    @Override
    public void tick() {
        super.tick();
        if(level == null || level.isClientSide) return;
        if(!isPlaying() && isPowered && hasPlayableItem){
            startPlaying();
        }
        if(isPlaying() && !isPowered){
            stopPlaying();
        }
    }

    @Override
    @ParametersAreNonnullByDefault
    public void load(CompoundTag compound) {
        super.load(compound);
        isPowered = compound.getBoolean("isPowered");
        tickDelayCoef = compound.getInt("tickDelayCoef");
        tickDelayAddition = compound.getInt("tickDelayAddition");
    }

    @Override
    @ParametersAreNonnullByDefault
    protected void saveAdditional(CompoundTag compound) {
        super.saveAdditional(compound);
        compound.putBoolean("isPowered", isPowered);
        compound.putInt("tickDelayCoef", tickDelayCoef);
        compound.putInt("tickDelayAddition", tickDelayAddition);
    }
}
