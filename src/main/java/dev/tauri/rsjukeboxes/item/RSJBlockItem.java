package dev.tauri.rsjukeboxes.item;

import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.CreativeModeTab;
import net.minecraft.world.level.block.Block;
import net.minecraftforge.registries.RegistryObject;
import org.jetbrains.annotations.Nullable;

public class RSJBlockItem extends BlockItem implements ITabbedItem {

    private final RegistryObject<CreativeModeTab> tab;
    protected final Block rawBlock;

    public RSJBlockItem(Block pBlock, Properties pProperties, @Nullable RegistryObject<CreativeModeTab> tab) {
        super(pBlock, pProperties);
        this.tab = tab;
        this.rawBlock = pBlock;
    }

    @Override
    public RegistryObject<CreativeModeTab> getTab() {
        return tab;
    }

    public void addAdditional(CreativeModeTab.Output output) {
    }
}
