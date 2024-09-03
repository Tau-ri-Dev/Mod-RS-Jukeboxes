package dev.tauri.rsjukeboxes.blockentity;

import dev.tauri.rsjukeboxes.RSJukeboxes;
import dev.tauri.rsjukeboxes.registry.BlockEntityRegistry;
import net.minecraft.block.BlockState;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.BlockPos;

public class Tier3JukeboxBE extends AbstractTieredJukeboxBE {
    public Tier3JukeboxBE(BlockPos pPos, BlockState pBlockState) {
        super(BlockEntityRegistry.TIER3_ADVANCED_JUKEBOX, pPos, pBlockState);
    }

    @Override
    public int getContainerSize(){
        return 15;
    }

    @Override
    public Identifier getGuiBackground() {
        return new Identifier(RSJukeboxes.MOD_ID, "textures/gui/jukebox_tier_3_gui.png");
    }
}
