package dev.tauri.rsjukeboxes.registry;

import dev.tauri.rsjukeboxes.block.*;
import net.minecraft.block.Block;
import net.minecraft.client.renderer.block.model.ModelResourceLocation;
import net.minecraft.item.Item;
import net.minecraft.item.ItemBlock;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.client.event.ModelRegistryEvent;
import net.minecraftforge.client.model.ModelLoader;
import net.minecraftforge.event.RegistryEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.common.registry.GameRegistry;
import net.minecraftforge.registries.IForgeRegistry;

import java.util.Objects;

@Mod.EventBusSubscriber
public class BlockRegistry {

    public static final Block RS_JUKEBOX_BLOCK = new RSJukeboxBlock(); //REGISTER.register("rs_jukebox_block", RSJukeboxBlock::new);
    public static final Block REPEATING_JUKEBOX_BLOCK = new RepeatingJukeboxBlock(); //REGISTER.register("repeating_jukebox_block", RepeatingJukeboxBlock::new);
    public static final Block TIER1_ADVANCED_JUKEBOX_BLOCK = new Tier1JukeboxBlock(); //REGISTER.register("tier1_advanced_jukebox_block", Tier1JukeboxBlock::new);
    public static final Block TIER2_ADVANCED_JUKEBOX_BLOCK = new Tier2JukeboxBlock(); //REGISTER.register("tier2_advanced_jukebox_block", Tier2JukeboxBlock::new);
    public static final Block TIER3_ADVANCED_JUKEBOX_BLOCK = new Tier3JukeboxBlock(); //REGISTER.register("tier3_advanced_jukebox_block", Tier3JukeboxBlock::new);

    public static final Block[] BLOCKS = {
            RS_JUKEBOX_BLOCK,
            REPEATING_JUKEBOX_BLOCK,
            TIER1_ADVANCED_JUKEBOX_BLOCK,
            TIER2_ADVANCED_JUKEBOX_BLOCK,
            TIER3_ADVANCED_JUKEBOX_BLOCK,
    };

    public static void load() {
    }

    @SubscribeEvent
    public static void onRegisterBlocks(RegistryEvent.Register<Block> event) {
        IForgeRegistry<Block> registry = event.getRegistry();

        registry.registerAll(BLOCKS);

        for (Block block : BLOCKS) {
            if (block instanceof RSJBlock && block.hasTileEntity(block.getDefaultState())) {
                Class<? extends TileEntity> tileEntityClass = ((RSJBlock) block).getTileEntityClass();
                ResourceLocation key = block.getRegistryName();
                if (key != null && tileEntityClass != null)
                    GameRegistry.registerTileEntity(tileEntityClass, key);
            }
        }
    }

    @SubscribeEvent
    public static void onRegisterItems(RegistryEvent.Register<Item> event) {
        IForgeRegistry<Item> registry = event.getRegistry();

        for (Block block : BLOCKS) {
            registry.register(new ItemBlock(block).setRegistryName(block.getRegistryName()));
        }
    }

    @SubscribeEvent
    public static void onModelRegistry(ModelRegistryEvent event) {
        for (Block block : BLOCKS) {
            ModelLoader.setCustomModelResourceLocation(ItemBlock.getItemFromBlock(block), 0, new ModelResourceLocation(Objects.requireNonNull(block.getRegistryName()), "inventory"));
        }
    }
}
