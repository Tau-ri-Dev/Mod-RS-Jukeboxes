package dev.tauri.rsjukeboxes.blockentity;

import dev.tauri.rsjukeboxes.RSJukeboxes;
import dev.tauri.rsjukeboxes.registry.BlockEntityRegistry;
import net.minecraft.block.BlockState;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.BlockPos;

public class Tier2JukeboxBE extends AbstractTieredJukeboxBE {
    public Tier2JukeboxBE(BlockPos pPos, BlockState pBlockState) {
        super(BlockEntityRegistry.TIER2_ADVANCED_JUKEBOX, pPos, pBlockState);
    }

    @Override
    public int getContainerSize(){
        return 10;
    }

    @Override
    public Identifier getGuiBackground() {
        return new Identifier(RSJukeboxes.MOD_ID, "textures/gui/jukebox_tier_2_gui.png");
    }
}
