package dev.tauri.rsjukeboxes.blockentity;

import dev.tauri.rsjukeboxes.util.ITickable;
import net.minecraft.core.BlockPos;
import net.minecraft.core.NonNullList;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.Tag;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.tags.ItemTags;
import net.minecraft.world.Clearable;
import net.minecraft.world.Container;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.item.ItemEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.RecordItem;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.JukeboxBlock;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.gameevent.GameEvent;
import net.minecraft.world.phys.Vec3;
import org.jetbrains.annotations.NotNull;

import javax.annotation.Nullable;
import java.util.Objects;

public abstract class AbstractRSJukeboxBE extends BlockEntity implements Clearable, Container, ITickable {
    private final NonNullList<ItemStack> items = NonNullList.withSize(this.getContainerSize(), ItemStack.EMPTY);
    private int ticksSinceLastEvent;
    private long recordStartedTick;
    private boolean isPlaying;

    public AbstractRSJukeboxBE(BlockEntityType<?> type, BlockPos pPos, BlockState pBlockState) {
        super(type, pPos, pBlockState);
    }

    public ItemStack getCurrentPlayingDiscStack(){
        return getItem(0);
    }

    public boolean isRecordPlaying() {
        return !getCurrentPlayingDiscStack().isEmpty() && this.isPlaying;
    }

    private void setHasRecordBlockState(@Nullable Entity pEntity, boolean pHasRecord) {
        if (Objects.requireNonNull(this.level).getBlockState(this.getBlockPos()) == this.getBlockState()) {
            this.level.setBlock(this.getBlockPos(), this.getBlockState().setValue(JukeboxBlock.HAS_RECORD, pHasRecord), 2);
            this.level.gameEvent(GameEvent.BLOCK_CHANGE, this.getBlockPos(), GameEvent.Context.of(pEntity, this.getBlockState()));
        }
    }

    public boolean shouldPlay(){
        return !getItem(0).isEmpty();
    }

    public void startPlaying() {
        if(!shouldPlay()) return;
        this.setHasRecordBlockState(null, true);
        this.recordStartedTick = Objects.requireNonNull(level).getGameTime();
        this.isPlaying = true;
        Objects.requireNonNull(this.level).updateNeighborsAt(this.getBlockPos(), this.getBlockState().getBlock());
        this.level.levelEvent(null, 1010, this.getBlockPos(), Item.getId(getCurrentPlayingDiscStack().getItem()));
        this.setChanged();
    }

    private void stopPlaying() {
        this.setHasRecordBlockState(null, false);
        this.isPlaying = false;
        Objects.requireNonNull(this.level).gameEvent(GameEvent.JUKEBOX_STOP_PLAY, this.getBlockPos(), GameEvent.Context.of(this.getBlockState()));
        this.level.updateNeighborsAt(this.getBlockPos(), this.getBlockState().getBlock());
        this.level.levelEvent(1011, this.getBlockPos(), 0);
        this.setChanged();
    }

    public void tick() {
        ++this.ticksSinceLastEvent;
        if (this.isRecordPlaying()) {
            Item item = getCurrentPlayingDiscStack().getItem();
            if (item instanceof RecordItem recorditem) {
                if (this.shouldRecordStopPlaying(recorditem)) {
                    this.stopPlaying();
                } else if (this.shouldSendJukeboxPlayingEvent()) {
                    this.ticksSinceLastEvent = 0;
                    Objects.requireNonNull(level).gameEvent(GameEvent.JUKEBOX_PLAY, getBlockPos(), GameEvent.Context.of(level.getBlockState(getBlockPos())));
                    this.spawnMusicParticles(level, getBlockPos());
                }
            }
        }
    }

    public long getDelayBetweenRecords(){
        return 20L;
    }

    private boolean shouldRecordStopPlaying(RecordItem pRecord) {
        return Objects.requireNonNull(level).getGameTime() >= this.recordStartedTick + (long) pRecord.getLengthInTicks() + getDelayBetweenRecords();
    }

    private boolean shouldSendJukeboxPlayingEvent() {
        return this.ticksSinceLastEvent >= 20;
    }

    @Override
    public boolean isEmpty() {
        for(int i = 0; i < getContainerSize(); i++) {
            if(!getItem(i).isEmpty()) return false;
        }
        return true;
    }

    @Override
    public void clearContent() {
        for(int i = 0; i < getContainerSize(); i++) {
            setItem(i, ItemStack.EMPTY);
        }
    }

    public void popOutRecords(){
        if (this.level != null && !this.level.isClientSide) {
            BlockPos blockpos = this.getBlockPos();
            for(int i = 0; i < getContainerSize(); i++) {
                ItemStack stack = getItem(i).copy();
                setItem(i, ItemStack.EMPTY);
                if (!stack.isEmpty()) {
                    Vec3 vec3 = Vec3.atLowerCornerWithOffset(blockpos, 0.5D, 1.01D, 0.5D).offsetRandom(this.level.random, 0.7F);
                    ItemEntity itementity = new ItemEntity(this.level, vec3.x(), vec3.y(), vec3.z(), stack);
                    itementity.setDefaultPickUpDelay();
                    this.level.addFreshEntity(itementity);
                }
            }
        }
    }
    public @NotNull ItemStack getItem(int pSlot) {
        return this.items.get(pSlot);
    }
    public @NotNull ItemStack removeItem(int pSlot, int pAmount) {
        ItemStack itemstack = Objects.requireNonNullElse(this.items.get(pSlot), ItemStack.EMPTY);
        this.items.set(pSlot, ItemStack.EMPTY);
        if (pSlot == 0 && !itemstack.isEmpty()) {
            this.stopPlaying();
        }

        return itemstack;
    }
    @Override
    public @NotNull ItemStack removeItemNoUpdate(int pSlot) {
        return removeItem(pSlot, 1);
    }

    public void setItem(int pSlot, ItemStack pStack) {
        if (pStack.is(ItemTags.MUSIC_DISCS) && this.level != null) {
            this.items.set(pSlot, pStack);
        }
    }

    public void setAndPlay(int pSlot, ItemStack pStack) {
        if (pStack.is(ItemTags.MUSIC_DISCS) && this.level != null) {
            setItem(pSlot, pStack.copy());
            pStack.shrink(1);
            if(pSlot == 0){
                level.gameEvent(GameEvent.BLOCK_CHANGE, getBlockPos(), GameEvent.Context.of(null, getBlockState()));
                if(isPlaying) stopPlaying();
            }
        }
    }

    public int getMaxStackSize() {
        return 1;
    }

    public boolean stillValid(@NotNull Player pPlayer) {
        return Container.stillValidBlockEntity(this, pPlayer);
    }
    public boolean canPlaceItem(int pIndex, ItemStack pStack) {
        return pStack.is(ItemTags.MUSIC_DISCS) && this.getItem(pIndex).isEmpty();
    }
    public boolean canTakeItem(Container pContainer, int pIndex, @NotNull ItemStack pStack) {
        return pContainer.hasAnyMatching(ItemStack::isEmpty);
    }
    private void spawnMusicParticles(Level pLevel, BlockPos pPos) {
        if (pLevel instanceof ServerLevel serverlevel) {
            Vec3 vec3 = Vec3.atBottomCenterOf(pPos).add(0.0D, (double) 1.2F, 0.0D);
            float f = (float) pLevel.getRandom().nextInt(4) / 24.0F;
            serverlevel.sendParticles(ParticleTypes.NOTE, vec3.x(), vec3.y(), vec3.z(), 0, (double) f, 0.0D, 0.0D, 1.0D);
        }

    }
    public void load(@NotNull CompoundTag pTag) {
        super.load(pTag);
        for(var i = 0; i < getContainerSize(); i++) {
            if (pTag.contains("RecordItem" + i, Tag.TAG_COMPOUND)) {
                this.items.set(i, ItemStack.of(pTag.getCompound("RecordItem" + i)));
            }
        }

        this.isPlaying = pTag.getBoolean("IsPlaying");
        this.recordStartedTick = pTag.getLong("RecordStartTick");
    }

    protected void saveAdditional(@NotNull CompoundTag pTag) {
        super.saveAdditional(pTag);
        for(var i = 0; i < getContainerSize(); i++){
            if (!getItem(i).isEmpty()) {
                pTag.put("RecordItem" + i, getItem(i).save(new CompoundTag()));
            }
        }

        pTag.putBoolean("IsPlaying", this.isPlaying);
        pTag.putLong("RecordStartTick", this.recordStartedTick);
    }
}
