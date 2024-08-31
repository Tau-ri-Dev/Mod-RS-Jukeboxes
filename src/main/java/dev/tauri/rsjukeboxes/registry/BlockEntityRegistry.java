package dev.tauri.rsjukeboxes.registry;

import dev.tauri.rsjukeboxes.blockentity.*;
import dev.tauri.rsjukeboxes.renderer.TieredJukeboxRenderer;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraftforge.client.event.EntityRenderersEvent;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.RegistryObject;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Supplier;

import static dev.tauri.rsjukeboxes.RSJukeboxes.MOD_ID;

public class BlockEntityRegistry {
    public static final DeferredRegister<BlockEntityType<?>> REGISTER = DeferredRegister.create(ForgeRegistries.BLOCK_ENTITY_TYPES, MOD_ID);


    public static final RegistryObject<BlockEntityType<RSJukeboxBE>> RS_JUKEBOX = registerBE("rs_jukebox", RSJukeboxBE::new, BlockRegistry.RS_JUKEBOX_BLOCK);
    public static final RegistryObject<BlockEntityType<RepeatingJukeboxBE>> REPEATING_JUKEBOX = registerBE("repeating_jukebox", RepeatingJukeboxBE::new, BlockRegistry.REPEATING_JUKEBOX_BLOCK);
    public static final RegistryObject<BlockEntityType<Tier1JukeboxBE>> TIER1_ADVANCED_JUKEBOX = registerBE("tier1_advanced_jukebox", Tier1JukeboxBE::new, BlockRegistry.TIER1_ADVANCED_JUKEBOX_BLOCK);
    public static final RegistryObject<BlockEntityType<Tier2JukeboxBE>> TIER2_ADVANCED_JUKEBOX = registerBE("tier2_advanced_jukebox", Tier2JukeboxBE::new, BlockRegistry.TIER2_ADVANCED_JUKEBOX_BLOCK);
    public static final RegistryObject<BlockEntityType<Tier3JukeboxBE>> TIER3_ADVANCED_JUKEBOX = registerBE("tier3_advanced_jukebox", Tier3JukeboxBE::new, BlockRegistry.TIER3_ADVANCED_JUKEBOX_BLOCK);


    public static <T extends BlockEntity> RegistryObject<BlockEntityType<T>> registerBE(String name, BlockEntityType.BlockEntitySupplier<T> beSupplier, Supplier<? extends Block> blockSupplier) {
        return registerBE(name, beSupplier, List.of(blockSupplier));
    }

    public static <T extends BlockEntity> RegistryObject<BlockEntityType<T>> registerBE(String name, BlockEntityType.BlockEntitySupplier<T> beSupplier, List<? extends Supplier<? extends Block>> blockSuppliers) {
        return REGISTER.register(name, () -> {
            List<Block> blocks = new ArrayList<>();
            for (var object : blockSuppliers) {
                blocks.add(object.get());
            }
            return BlockEntityType.Builder.of(beSupplier, blocks.toArray(new Block[0])).build(null);
        });
    }


    public static void register(IEventBus bus) {
        REGISTER.register(bus);
    }

    @SubscribeEvent
    public static void registerBERs(EntityRenderersEvent.RegisterRenderers event) {
        event.registerBlockEntityRenderer(TIER1_ADVANCED_JUKEBOX.get(), TieredJukeboxRenderer::new);
        event.registerBlockEntityRenderer(TIER2_ADVANCED_JUKEBOX.get(), TieredJukeboxRenderer::new);
        event.registerBlockEntityRenderer(TIER3_ADVANCED_JUKEBOX.get(), TieredJukeboxRenderer::new);
    }
}
