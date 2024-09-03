package dev.tauri.rsjukeboxes.registry;

import dev.tauri.rsjukeboxes.blockentity.*;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.block.entity.BlockEntityType;
import net.minecraft.registry.Registries;
import net.minecraft.registry.Registry;
import net.minecraft.util.Identifier;

import static dev.tauri.rsjukeboxes.RSJukeboxes.MOD_ID;

public class BlockEntityRegistry {

    public static final BlockEntityType<RSJukeboxBE> RS_JUKEBOX = registerBE("rs_jukebox", BlockEntityType.Builder.create(RSJukeboxBE::new, BlockRegistry.RS_JUKEBOX_BLOCK).build(null));
    public static final BlockEntityType<RepeatingJukeboxBE> REPEATING_JUKEBOX = registerBE("repeating_jukebox", BlockEntityType.Builder.create(RepeatingJukeboxBE::new, BlockRegistry.REPEATING_JUKEBOX_BLOCK).build(null));
    public static final BlockEntityType<Tier1JukeboxBE> TIER1_ADVANCED_JUKEBOX = registerBE("tier1_advanced_jukebox", BlockEntityType.Builder.create(Tier1JukeboxBE::new, BlockRegistry.TIER1_ADVANCED_JUKEBOX_BLOCK).build(null));
    public static final BlockEntityType<Tier2JukeboxBE> TIER2_ADVANCED_JUKEBOX = registerBE("tier2_advanced_jukebox", BlockEntityType.Builder.create(Tier2JukeboxBE::new, BlockRegistry.TIER2_ADVANCED_JUKEBOX_BLOCK).build(null));
    public static final BlockEntityType<Tier3JukeboxBE> TIER3_ADVANCED_JUKEBOX = registerBE("tier3_advanced_jukebox", BlockEntityType.Builder.create(Tier3JukeboxBE::new, BlockRegistry.TIER3_ADVANCED_JUKEBOX_BLOCK).build(null));


    public static <T extends BlockEntity> BlockEntityType<T> registerBE(String name, BlockEntityType<T> type) {
        return Registry.register(Registries.BLOCK_ENTITY_TYPE, new Identifier(MOD_ID, name), type);
    }


    public static void register() {
    }
}
