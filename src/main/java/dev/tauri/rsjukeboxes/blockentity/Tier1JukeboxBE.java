package dev.tauri.rsjukeboxes.blockentity;

import dev.tauri.rsjukeboxes.RSJukeboxes;
import dev.tauri.rsjukeboxes.registry.BlockEntityRegistry;
import net.minecraft.block.BlockState;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.BlockPos;

public class Tier1JukeboxBE extends AbstractTieredJukeboxBE {
    public Tier1JukeboxBE(BlockPos pPos, BlockState pBlockState) {
        super(BlockEntityRegistry.TIER1_ADVANCED_JUKEBOX, pPos, pBlockState);
    }

    @Override
    public int getContainerSize(){
        return 5;
    }

    @Override
    public Identifier getGuiBackground() {
        return new Identifier(RSJukeboxes.MOD_ID, "textures/gui/jukebox_tier_1_gui.png");
    }
}
