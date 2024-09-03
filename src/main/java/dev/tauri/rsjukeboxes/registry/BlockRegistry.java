package dev.tauri.rsjukeboxes.registry;

import dev.tauri.rsjukeboxes.RSJukeboxes;
import dev.tauri.rsjukeboxes.block.*;
import dev.tauri.rsjukeboxes.item.ITabbedItem;
import dev.tauri.rsjukeboxes.item.RSJBlockItem;
import net.minecraft.block.Block;
import net.minecraft.item.Item;
import net.minecraft.item.ItemGroup;
import net.minecraft.registry.Registries;
import net.minecraft.registry.Registry;
import net.minecraft.util.Identifier;

import java.util.function.Supplier;

public class BlockRegistry {

    public static final Block RS_JUKEBOX_BLOCK = registerBlock("rs_jukebox_block", RSJukeboxBlock::new);
    public static final Block REPEATING_JUKEBOX_BLOCK = registerBlock("repeating_jukebox_block", RepeatingJukeboxBlock::new);
    public static final Block TIER1_ADVANCED_JUKEBOX_BLOCK = registerBlock("tier1_advanced_jukebox_block", Tier1JukeboxBlock::new);
    public static final Block TIER2_ADVANCED_JUKEBOX_BLOCK = registerBlock("tier2_advanced_jukebox_block", Tier2JukeboxBlock::new);
    public static final Block TIER3_ADVANCED_JUKEBOX_BLOCK = registerBlock("tier3_advanced_jukebox_block", Tier3JukeboxBlock::new);

    public static Block registerBlock(String name, Supplier<Block> construct) {
        var block = Registry.register(Registries.BLOCK, new Identifier(RSJukeboxes.MOD_ID, name), construct.get());
        Supplier<ItemGroup> tab = null;
        if (block instanceof ITabbedItem t) {
            tab = t.getTab();
        }
        var item = new RSJBlockItem(block, new Item.Settings(), tab);
        ItemRegistry.registerItem(name, item);
        return block;
    }


    public static void register() {
    }
}
