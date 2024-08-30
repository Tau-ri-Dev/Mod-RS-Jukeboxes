package dev.tauri.rsjukeboxes.util;

import net.minecraft.core.BlockPos;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.item.ItemEntity;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraftforge.items.IItemHandler;

import java.util.Random;

public class ItemHandlerHelper {
    private static final Random RANDOM = new Random();

    public static void dropInventoryItems(Level worldIn, BlockPos pos, IItemHandler inventory) {
        dropInventoryItems(worldIn, (double) pos.getX(), (double) pos.getY(), (double) pos.getZ(), inventory);
    }

    public static void dropInventoryItems(Level worldIn, Entity entityAt, IItemHandler inventory) {
        dropInventoryItems(worldIn, entityAt.getX(), entityAt.getY(), entityAt.getZ(), inventory);
    }

    private static void dropInventoryItems(Level worldIn, double x, double y, double z, IItemHandler inventory) {
        for (int i = 0; i < inventory.getSlots(); ++i) {
            ItemStack itemstack = inventory.getStackInSlot(i);

            if (!itemstack.isEmpty()) {
                spawnItemStack(worldIn, x, y, z, itemstack);
            }
        }
    }

    public static void clearInventory(IItemHandler inventory){
        for (int i = 0; i < inventory.getSlots(); ++i) {
            ItemStack itemstack = inventory.getStackInSlot(i);

            if (!itemstack.isEmpty()) {
                itemstack.setCount(0);
            }
        }
    }

    public static void spawnItemStack(Level worldIn, BlockPos pos, ItemStack stack) {
        spawnItemStack(worldIn, pos.getX(), pos.getY(), pos.getZ(), stack);
    }
    public static void spawnItemStack(Level worldIn, double x, double y, double z, ItemStack stack) {
        float f = RANDOM.nextFloat() * 0.8F + 0.1F;
        float f1 = RANDOM.nextFloat() * 0.8F + 0.1F;
        float f2 = RANDOM.nextFloat() * 0.8F + 0.1F;

        while (!stack.isEmpty()) {
            ItemEntity entityitem = new ItemEntity(worldIn, x + (double) f, y + (double) f1, z + (double) f2, stack.split(RANDOM.nextInt(21) + 10));
            double motionX = RANDOM.nextGaussian() * 0.05000000074505806D;
            double motionY = RANDOM.nextGaussian() * 0.05000000074505806D + 0.20000000298023224D;
            double motionZ = RANDOM.nextGaussian() * 0.05000000074505806D;
            entityitem.setDeltaMovement(motionX, motionY, motionZ);
            worldIn.addFreshEntity(entityitem);
        }
    }
}