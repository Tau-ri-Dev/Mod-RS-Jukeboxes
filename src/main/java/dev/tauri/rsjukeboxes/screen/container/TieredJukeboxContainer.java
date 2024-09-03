package dev.tauri.rsjukeboxes.screen.container;

import dev.tauri.rsjukeboxes.blockentity.AbstractTieredJukeboxBE;
import dev.tauri.rsjukeboxes.packet.RSJPacketHandler;
import dev.tauri.rsjukeboxes.packet.packets.StateUpdatePacketToClient;
import dev.tauri.rsjukeboxes.registry.MenuTypeRegistry;
import dev.tauri.rsjukeboxes.screen.util.ContainerHelper;
import dev.tauri.rsjukeboxes.state.StateTypeEnum;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.item.ItemStack;
import net.minecraft.network.PacketByteBuf;
import net.minecraft.screen.ScreenHandler;
import net.minecraft.screen.slot.Slot;
import net.minecraft.server.network.ServerPlayerEntity;
import org.jetbrains.annotations.NotNull;

public class TieredJukeboxContainer extends ScreenHandler {
    public final PlayerInventory playerInventory;

    public final AbstractTieredJukeboxBE jukebox;

    private int lastCurrentSlot = -1;

    public TieredJukeboxContainer(int containerID, PlayerInventory playerInventory, BlockEntity blockEntity) {
        super(MenuTypeRegistry.TIERED_JUKEBOX_MENU_TYPE, containerID);
        this.playerInventory = playerInventory;
        if (blockEntity == null) {
            throw new NullPointerException("Jukebox BE is null inside the container! Can not continue!");
        }
        this.jukebox = (AbstractTieredJukeboxBE) blockEntity;

        int playerInvY = 0;
        for (int i = 0; i < jukebox.getContainerSize(); i++) {
            int x = 18 * (i % 5) + 8;
            int y = 18 * (i / 5) + 24;
            playerInvY = y + 42;
            addSlot(new Slot(jukebox.itemStackHandler, i, x, y));
        }

        for (Slot slot : ContainerHelper.generatePlayerSlots(playerInventory, playerInvY))
            addSlot(slot);
    }

    // Client
    public TieredJukeboxContainer(int syncId, PlayerInventory playerInventory, PacketByteBuf buf) {
        this(syncId, playerInventory, playerInventory.player.getWorld().getBlockEntity(buf.readBlockPos()));
    }

    @Override
    public @NotNull ItemStack quickMove(@NotNull PlayerEntity player, int index) {
        ItemStack stack = getSlot(index).getStack();
        var slotsCount = jukebox.getContainerSize();
        // Transferring to player's inventory
        if (index < slotsCount) {
            if (!insertItem(stack, slotsCount, slots.size(), false)) {
                return ItemStack.EMPTY;
            }
            getSlot(index).setStack(ItemStack.EMPTY);
            setPreviousTrackedSlot(index, ItemStack.EMPTY);
        }
        // Transferring from player's inventory
        else {
            for (int i = 0; i < slotsCount; i++) {
                if (jukebox.itemStackHandler.isValid(i, stack)) {
                    if (!getSlot(i).hasStack()) {
                        ItemStack stack1 = stack.copy();
                        stack1.setCount(1);
                        setPreviousTrackedSlot(i, stack1);
                        getSlot(i).setStack(stack1);
                        stack.decrement(1);
                        return ItemStack.EMPTY;
                    }
                }
            }
            return ItemStack.EMPTY;
        }

        return stack;
    }

    @Override
    public boolean canUse(@NotNull PlayerEntity pPlayer) {
        return true;
    }


    @Override
    public void sendContentUpdates() {
        super.sendContentUpdates();
        if (lastCurrentSlot != jukebox.getCurrentSlotPlaying()) {
            if (playerInventory.player instanceof ServerPlayerEntity sp)
                RSJPacketHandler.sendTo(new StateUpdatePacketToClient(jukebox.getPos(), StateTypeEnum.JUKEBOX_UPDATE, jukebox.getState(StateTypeEnum.JUKEBOX_UPDATE)), sp);
            lastCurrentSlot = jukebox.getCurrentSlotPlaying();
        }
    }
}
