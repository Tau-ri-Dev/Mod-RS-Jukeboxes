package dev.tauri.rsjukeboxes.screen.container;

import dev.tauri.rsjukeboxes.blockentity.AbstractTieredJukeboxBE;
import dev.tauri.rsjukeboxes.packet.RSJPacketHandler;
import dev.tauri.rsjukeboxes.packet.packets.StateUpdatePacketToClient;
import dev.tauri.rsjukeboxes.registry.MenuTypeRegistry;
import dev.tauri.rsjukeboxes.screen.util.ContainerHelper;
import dev.tauri.rsjukeboxes.state.StateTypeEnum;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.inventory.Slot;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraftforge.items.SlotItemHandler;
import org.jetbrains.annotations.NotNull;

import javax.annotation.ParametersAreNonnullByDefault;

public class TieredJukeboxContainer extends AbstractContainerMenu {
    public final Inventory playerInventory;

    public final AbstractTieredJukeboxBE jukebox;

    private int lastCurrentSlot = -1;

    public TieredJukeboxContainer(int containerID, Inventory playerInventory, BlockEntity blockEntity) {
        super(MenuTypeRegistry.TIERED_JUKEBOX_MENU_TYPE.get(), containerID);
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
            addSlot(new SlotItemHandler(jukebox.itemStackHandler, i, x, y));
        }

        for (Slot slot : ContainerHelper.generatePlayerSlots(playerInventory, playerInvY))
            addSlot(slot);
    }

    // Client
    public TieredJukeboxContainer(int containerID, Inventory playerInventory, FriendlyByteBuf buf) {
        this(containerID, playerInventory, playerInventory.player.level().getBlockEntity(buf.readBlockPos()));
    }

    @Override
    @ParametersAreNonnullByDefault
    public @NotNull ItemStack quickMoveStack(@NotNull Player player, int index) {
        ItemStack stack = getSlot(index).getItem();
        var slotsCount = jukebox.getContainerSize();
        // Transferring to player's inventory
        if (index < slotsCount) {
            if (!moveItemStackTo(stack, slotsCount, slots.size(), false)) {
                return ItemStack.EMPTY;
            }
            getSlot(index).set(ItemStack.EMPTY);
            setRemoteSlot(index, ItemStack.EMPTY);
        }
        // Transferring from player's inventory
        else {
            for (int i = 0; i < slotsCount; i++) {
                if (jukebox.itemStackHandler.isItemValid(i, stack)) {
                    if (!getSlot(i).hasItem()) {
                        ItemStack stack1 = stack.copy();
                        stack1.setCount(1);
                        setRemoteSlot(i, stack1);
                        getSlot(i).set(stack1);
                        stack.shrink(1);
                        return ItemStack.EMPTY;
                    }
                }
            }
            return ItemStack.EMPTY;
        }

        return stack;
    }

    @Override
    public boolean stillValid(@NotNull Player pPlayer) {
        return true;
    }


    @Override
    public void broadcastChanges() {
        super.broadcastChanges();
        if (lastCurrentSlot != jukebox.getCurrentSlotPlaying()) {
            if (playerInventory.player instanceof ServerPlayer sp)
                RSJPacketHandler.sendTo(new StateUpdatePacketToClient(jukebox.getBlockPos(), StateTypeEnum.JUKEBOX_UPDATE, jukebox.getState(StateTypeEnum.JUKEBOX_UPDATE)), sp);
            lastCurrentSlot = jukebox.getCurrentSlotPlaying();
        }
    }
}
