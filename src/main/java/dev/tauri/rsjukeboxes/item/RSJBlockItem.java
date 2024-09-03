package dev.tauri.rsjukeboxes.item;

import net.minecraft.block.Block;
import net.minecraft.item.BlockItem;
import net.minecraft.item.Item;
import net.minecraft.item.ItemGroup;
import org.jetbrains.annotations.Nullable;

import java.util.function.Supplier;

public class RSJBlockItem extends BlockItem implements ITabbedItem {

    private final Supplier<ItemGroup> tab;
    protected final Block rawBlock;

    public RSJBlockItem(Block pBlock, Item.Settings pProperties, @Nullable Supplier<ItemGroup> tab) {
        super(pBlock, pProperties);
        this.tab = tab;
        this.rawBlock = pBlock;
    }

    @Override
    public Supplier<ItemGroup> getTab() {
        return tab;
    }

    public void addAdditional(ItemGroup.Entries entries) {
    }
}
