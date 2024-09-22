package dev.tauri.rsjukeboxes.blockentity;

import dev.tauri.rsjukeboxes.RSJukeboxes;
import dev.tauri.rsjukeboxes.compatibility.VinURLCompat;
import dev.tauri.rsjukeboxes.packet.RSJPacketHandler;
import dev.tauri.rsjukeboxes.packet.RSJPacketHandlerClient;
import dev.tauri.rsjukeboxes.packet.packets.StateUpdatePacketToClient;
import dev.tauri.rsjukeboxes.packet.packets.StateUpdateRequestToServer;
import dev.tauri.rsjukeboxes.renderer.GenericRSJukeboxRendererState;
import dev.tauri.rsjukeboxes.state.State;
import dev.tauri.rsjukeboxes.state.StateProviderInterface;
import dev.tauri.rsjukeboxes.state.StateTypeEnum;
import dev.tauri.rsjukeboxes.util.ITickable;
import dev.tauri.rsjukeboxes.util.TargetPoint;
import net.minecraft.block.BlockState;
import net.minecraft.block.JukeboxBlock;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.block.entity.BlockEntityType;
import net.minecraft.entity.ItemEntity;
import net.minecraft.inventory.SimpleInventory;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.item.MusicDiscItem;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.nbt.NbtElement;
import net.minecraft.particle.ParticleTypes;
import net.minecraft.registry.tag.ItemTags;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.World;
import net.minecraft.world.WorldEvents;
import net.minecraft.world.event.GameEvent;

import java.util.Objects;

public class AbstractRSJukeboxBE extends BlockEntity implements ITickable, StateProviderInterface {
    public AbstractRSJukeboxBE(BlockEntityType<?> type, BlockPos pPos, BlockState pBlockState) {
        super(type, pPos, pBlockState);
    }

    public long getDelayBetweenRecords() {
        return 20L;
    }

    public boolean isPlaying() {
        return isPlaying;
    }

    public boolean hasPlayableItem() {
        return hasPlayableItem != null && hasPlayableItem;
    }

    protected boolean isPlaying = false;
    protected Boolean hasPlayableItem = null;
    public long playingStarted;
    public long playingStopped;

    protected int currentSlotPlaying = 0;

    public int getCurrentSlotPlaying() {
        return Math.max(0, Math.min(getContainerSize() - 1, currentSlotPlaying));
    }

    protected GenericRSJukeboxRendererState rendererState = new GenericRSJukeboxRendererState();

    public GenericRSJukeboxRendererState getRendererState() {
        return rendererState;
    }

    public void popOutRecords() {
        for (int i = 0; i < getContainerSize(); i++) {
            popOutRecord(i);
        }
    }

    public void setChanged() {
        markDirty();
    }

    public void popOutRecord(int slot) {
        if (this.world != null && !this.world.isClient) {
            BlockPos blockpos = this.getPos();
            ItemStack stack = itemStackHandler.getStack(slot).copy();
            itemStackHandler.setStack(slot, ItemStack.EMPTY);
            if (!stack.isEmpty()) {
                Vec3d vec3 = Vec3d.add(blockpos, 0.5D, 1.01D, 0.5D).addRandom(this.world.random, 0.7F);
                ItemEntity itementity = new ItemEntity(this.world, vec3.getX(), vec3.getY(), vec3.getZ(), stack);
                itementity.setToDefaultPickupDelay();
                this.world.spawnEntity(itementity);
            }
            if (slot == getCurrentSlotPlaying()) stopPlaying();
        }
    }

    public void spawnMusicParticles(World pLevel, BlockPos pPos, BlockState pState) {
        if (pLevel instanceof ServerWorld serverWorld) {
            Vec3d vec3 = Vec3d.of(pPos).add(0.0D, 1.2F, 0.0D);
            float f = (float) pLevel.getRandom().nextInt(4) / 24.0F;
            serverWorld.spawnParticles(ParticleTypes.NOTE, vec3.getX(), vec3.getY(), vec3.getZ(), 0, f, 0.0D, 0.0D, 1.0D);
            pLevel.emitGameEvent(GameEvent.JUKEBOX_PLAY, pPos, GameEvent.Emitter.of(pState));
        }
    }

    protected boolean shouldRecordStopPlaying() {
        if (!isPlaying) return false;
        if (getPlayingItem().isEmpty()) return true;
        return Objects.requireNonNull(world).getTime() >= (this.playingStarted + (long) ((MusicDiscItem) getPlayingItem().getItem()).getSongLengthInTicks() + getDelayBetweenRecords() + (VinURLCompat.isCustomDisc(getPlayingItem().getItem()) ? 3 * 60 * 20 : 0));
    }

    protected void setHasRecordBlockState(boolean pHasRecord) {
        if (Objects.requireNonNull(this.world).getBlockState(this.getPos()) == this.getCachedState()) {
            this.world.setBlockState(this.getPos(), this.getCachedState().with(JukeboxBlock.HAS_RECORD, pHasRecord), 2);
            this.world.emitGameEvent(GameEvent.BLOCK_CHANGE, this.getPos(), GameEvent.Emitter.of(null, this.getCachedState()));
        }
    }

    public void startPlaying() {
        if (isPlaying) return;
        isPlaying = true;
        if (getPlayingItem().isEmpty()) return;

        VinURLCompat.playDisc(this);

        playingStarted = Objects.requireNonNull(world).getTime();
        this.markDirty();
        this.world.updateNeighborsAlways(this.getPos(), this.getCachedState().getBlock());
        this.world.syncWorldEvent(WorldEvents.JUKEBOX_STARTS_PLAYING, getPos(), Item.getRawId(this.getPlayingItem().getItem()));
        sendUpdate();
    }

    public void stopPlaying() {
        if (!isPlaying) return;
        isPlaying = false;

        VinURLCompat.stopDisc(this);

        Objects.requireNonNull(this.world).emitGameEvent(GameEvent.JUKEBOX_STOP_PLAY, this.getPos(), GameEvent.Emitter.of(this.getCachedState()));
        this.playingStopped = world.getTime();
        this.markDirty();
        this.world.updateNeighborsAlways(this.getPos(), this.getCachedState().getBlock());
        this.world.syncWorldEvent(WorldEvents.JUKEBOX_STOPS_PLAYING, getPos(), 0);
        sendUpdate();
    }

    public int getContainerSize() {
        return 1;
    }

    public final SimpleInventory itemStackHandler = new SimpleInventory(getContainerSize()) {
        @Override
        public boolean isValid(int slot, ItemStack stack) {
            return stack.isIn(ItemTags.MUSIC_DISCS);
        }

        @Override
        public void markDirty() {
            super.markDirty();
            AbstractRSJukeboxBE.this.markDirty();
            sendUpdate();
        }
    };

    public ItemStack getPlayingItem() {
        return itemStackHandler.getStack(getCurrentSlotPlaying());
    }

    @Override
    public void tick() {
        if (world == null || world.isClient()) return;
        if (shouldRecordStopPlaying()) {
            stopPlaying();
        }
        if (isPlaying && world.getTime() % 20 == 0) {
            spawnMusicParticles(world, getPos(), world.getBlockState(getPos()));
        }
        boolean hasPlayableItem = !getPlayingItem().isEmpty();
        if (this.hasPlayableItem == null || hasPlayableItem != this.hasPlayableItem) {
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
        rendererState.discInserted = !getPlayingItem().isEmpty();
        rendererState.discItemId = Item.getRawId(getPlayingItem().getItem());
        rendererState.playing = isPlaying;
        rendererState.playingStarted = playingStarted;
        rendererState.playingStopped = playingStopped;
        rendererState.selectedSlot = currentSlotPlaying;
        return rendererState;
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
        return new GenericRSJukeboxRendererState();
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
        if (state instanceof GenericRSJukeboxRendererState newState) {
            rendererState = newState;
            markDirty();
        }
    }

    protected TargetPoint targetPoint;

    @Override
    public void cancelRemoval() {
        super.cancelRemoval();
        if (world == null) return;
        if (!world.isClient) {
            var pos = getPos();
            targetPoint = new TargetPoint(pos.getX(), pos.getY(), pos.getZ(), 512, (ServerWorld) world);
        }
        sendUpdate();
        sendRequest();
    }

    public void sendUpdate() {
        if (world == null) return;
        if (world.isClient) return;
        sendState(StateTypeEnum.JUKEBOX_UPDATE, getState(StateTypeEnum.JUKEBOX_UPDATE));
    }

    public void sendRequest() {
        if (world == null) return;
        if (!world.isClient) return;
        RSJPacketHandlerClient.sendToServer(new StateUpdateRequestToServer(getPos(), StateTypeEnum.JUKEBOX_UPDATE));
    }

    public void sendState(StateTypeEnum type, State state) {
        if (world == null || world.isClient) return;

        if (targetPoint != null) {
            RSJPacketHandler.sendToClient(new StateUpdatePacketToClient(getPos(), type, state), targetPoint);
        } else {
            RSJukeboxes.logger.debug("targetPoint was null trying to send " + type + " from " + this.getClass().getCanonicalName());
        }
    }


    @Override
    public void readNbt(NbtCompound compound) {
        super.readNbt(compound);
        itemStackHandler.readNbtList(compound.getList("itemStackHandler", NbtElement.COMPOUND_TYPE));
        isPlaying = compound.getBoolean("isPlaying");
        playingStarted = compound.getLong("playingStarted");
        playingStopped = compound.getLong("playingStopped");
        currentSlotPlaying = compound.getInt("currentSlotPlaying");
    }

    @Override
    protected void writeNbt(NbtCompound compound) {
        super.writeNbt(compound);
        compound.put("itemStackHandler", itemStackHandler.toNbtList());
        compound.putBoolean("isPlaying", isPlaying);
        compound.putLong("playingStarted", playingStarted);
        compound.putLong("playingStopped", playingStopped);
        compound.putInt("currentSlotPlaying", currentSlotPlaying);
    }
}
