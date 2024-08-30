package dev.tauri.rsjukeboxes.blockentity;

import dev.tauri.rsjukeboxes.RSJukeboxes;
import dev.tauri.rsjukeboxes.packet.RSJPacketHandler;
import dev.tauri.rsjukeboxes.packet.packets.StateUpdatePacketToClient;
import dev.tauri.rsjukeboxes.state.State;
import dev.tauri.rsjukeboxes.state.StateProviderInterface;
import dev.tauri.rsjukeboxes.state.StateTypeEnum;
import dev.tauri.rsjukeboxes.util.ITickable;
import net.minecraft.core.BlockPos;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.tags.ItemTags;
import net.minecraft.world.entity.item.ItemEntity;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.RecordItem;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.JukeboxBlock;
import net.minecraft.world.level.block.LevelEvent;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.gameevent.GameEvent;
import net.minecraft.world.phys.Vec3;
import net.minecraftforge.common.capabilities.ICapabilityProvider;
import net.minecraftforge.items.ItemStackHandler;
import net.minecraftforge.network.PacketDistributor;

import javax.annotation.ParametersAreNonnullByDefault;
import java.util.Objects;

public class AbstractRSJukeboxBE extends BlockEntity implements ITickable, ICapabilityProvider, StateProviderInterface {
    public AbstractRSJukeboxBE(BlockEntityType<?> type, BlockPos pPos, BlockState pBlockState) {
        super(type, pPos, pBlockState);
    }

    public long getDelayBetweenRecords() {
        return 20L;
    }

    public boolean isPlaying() {
        return isPlaying;
    }

    private boolean isPlaying = false;
    private boolean hasPlayableItem = false;
    private long recordStartedTick;

    public void popOutRecords() {
        for (int i = 0; i < getContainerSize(); i++) {
            popOutRecord(i);
        }
    }

    public void popOutRecord(int slot) {
        if (this.level != null && !this.level.isClientSide) {
            BlockPos blockpos = this.getBlockPos();
            ItemStack stack = itemStackHandler.getStackInSlot(slot).copy();
            itemStackHandler.setStackInSlot(slot, ItemStack.EMPTY);
            if (!stack.isEmpty()) {
                Vec3 vec3 = Vec3.atLowerCornerWithOffset(blockpos, 0.5D, 1.01D, 0.5D).offsetRandom(this.level.random, 0.7F);
                ItemEntity itementity = new ItemEntity(this.level, vec3.x(), vec3.y(), vec3.z(), stack);
                itementity.setDefaultPickUpDelay();
                this.level.addFreshEntity(itementity);
            }
            if(slot == 0) stopPlaying();
        }
    }

    private void spawnMusicParticles(Level pLevel, BlockPos pPos, BlockState pState) {
        if (pLevel instanceof ServerLevel serverlevel) {
            Vec3 vec3 = Vec3.atBottomCenterOf(pPos).add(0.0D, (double) 1.2F, 0.0D);
            float f = (float) pLevel.getRandom().nextInt(4) / 24.0F;
            serverlevel.sendParticles(ParticleTypes.NOTE, vec3.x(), vec3.y(), vec3.z(), 0, (double) f, 0.0D, 0.0D, 1.0D);
            pLevel.gameEvent(GameEvent.JUKEBOX_PLAY, pPos, GameEvent.Context.of(pState));
        }
    }

    private boolean shouldRecordStopPlaying() {
        if (!isPlaying) return false;
        if (getFirstItem().isEmpty()) return true;
        return Objects.requireNonNull(level).getGameTime() >= this.recordStartedTick + (long) ((RecordItem) getFirstItem().getItem()).getLengthInTicks() + getDelayBetweenRecords();
    }

    private void setHasRecordBlockState(boolean pHasRecord) {
        if (Objects.requireNonNull(this.level).getBlockState(this.getBlockPos()) == this.getBlockState()) {
            this.level.setBlock(this.getBlockPos(), this.getBlockState().setValue(JukeboxBlock.HAS_RECORD, pHasRecord), 2);
            this.level.gameEvent(GameEvent.BLOCK_CHANGE, this.getBlockPos(), GameEvent.Context.of(null, this.getBlockState()));
        }
    }

    public void startPlaying() {
        if(isPlaying) return;
        isPlaying = true;
        if (getFirstItem().isEmpty()) return;
        Objects.requireNonNull(this.level).updateNeighborsAt(this.getBlockPos(), this.getBlockState().getBlock());
        this.level.levelEvent(LevelEvent.SOUND_PLAY_JUKEBOX_SONG, getBlockPos(), Item.getId(this.getFirstItem().getItem()));
        recordStartedTick = level.getGameTime();
        this.setChanged();
    }

    private void stopPlaying() {
        if(!isPlaying) return;
        isPlaying = false;
        Objects.requireNonNull(this.level).gameEvent(GameEvent.JUKEBOX_STOP_PLAY, this.getBlockPos(), GameEvent.Context.of(this.getBlockState()));
        this.level.updateNeighborsAt(this.getBlockPos(), this.getBlockState().getBlock());
        this.level.levelEvent(LevelEvent.SOUND_STOP_JUKEBOX_SONG, getBlockPos(), 0);
        this.setChanged();
    }

    public int getContainerSize() {
        return 1;
    }

    public final ItemStackHandler itemStackHandler = new ItemStackHandler(getContainerSize()) {
        @Override
        public boolean isItemValid(int slot, ItemStack stack) {
            return stack.is(ItemTags.MUSIC_DISCS);
        }
        @Override
        protected void onContentsChanged(int slot){
            setChanged();
        }
    };

    public ItemStack getFirstItem() {
        return itemStackHandler.getStackInSlot(0);
    }

    @Override
    public void tick() {
        if (level == null || level.isClientSide) return;
        if (shouldRecordStopPlaying()) {
            stopPlaying();
        }
        if (isPlaying && level.getGameTime() % 20 == 0) {
            spawnMusicParticles(level, getBlockPos(), getBlockState());
        }
        boolean hasPlayableItem = !getFirstItem().isEmpty();
        if(hasPlayableItem != this.hasPlayableItem){
            setHasRecordBlockState(hasPlayableItem);
            this.hasPlayableItem = hasPlayableItem;
        }
    }

    /**
     * Server-side method. Called on {@link BlockEntity} to get specified {@link State}.
     *
     * @param stateType {@link StateTypeEnum} State to be collected/returned
     * @return {@link State} instance
     */
    @Override
    public State getState(StateTypeEnum stateType) {
        return null;
    }

    /**
     * Client-side method. Called on {@link BlockEntity} to get specified {@link State} instance
     * to recreate State by deserialization
     *
     * @param stateType {@link StateTypeEnum} State to be deserialized
     * @return deserialized {@link State}
     */
    @Override
    public State createState(StateTypeEnum stateType) {
        return null;
    }

    /**
     * Client-side method. Sets appropriate fields in client-side tile entity for it
     * to mirror the server-side tile entity
     *
     * @param stateType {@link StateTypeEnum} State to be applied
     * @param state     {@link State} instance obtained from packet
     */
    @Override
    public void setState(StateTypeEnum stateType, State state) {

    }

    protected PacketDistributor.TargetPoint targetPoint;

    @Override
    public void onLoad() {
        if (level != null && !level.isClientSide) {
            var pos = getBlockPos();
            targetPoint = new PacketDistributor.TargetPoint(pos.getX(), pos.getY(), pos.getZ(), 512, level.dimension());
        }
    }

    public void sendState(StateTypeEnum type, State state) {
        if (level == null || level.isClientSide) return;

        if (targetPoint != null) {
            RSJPacketHandler.sendToClient(new StateUpdatePacketToClient(getBlockPos(), type, state), targetPoint);
        } else {
            RSJukeboxes.logger.debug("targetPoint was null trying to send " + type + " from " + this.getClass().getCanonicalName());
        }
    }


    @Override
    @ParametersAreNonnullByDefault
    public void load(CompoundTag compound) {
        super.load(compound);
        itemStackHandler.deserializeNBT(compound.getCompound("itemStackHandler"));
        isPlaying = compound.getBoolean("isPlaying");
        recordStartedTick = compound.getLong("recordStartedTick");
        hasPlayableItem = compound.getBoolean("hasPlayableItem");
    }

    @Override
    @ParametersAreNonnullByDefault
    protected void saveAdditional(CompoundTag compound) {
        super.saveAdditional(compound);
        compound.put("itemStackHandler", itemStackHandler.serializeNBT());
        compound.putBoolean("isPlaying", isPlaying);
        compound.putLong("recordStartedTick", recordStartedTick);
        compound.putBoolean("hasPlayableItem", hasPlayableItem);
    }
}
