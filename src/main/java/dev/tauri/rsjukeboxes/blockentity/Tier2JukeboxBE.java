package dev.tauri.rsjukeboxes.blockentity;

import dev.tauri.rsjukeboxes.RSJukeboxes;
import dev.tauri.rsjukeboxes.registry.BlockEntityRegistry;
import net.minecraft.core.BlockPos;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.level.block.state.BlockState;

public class Tier2JukeboxBE extends AbstractTieredJukeboxBE {
    public Tier2JukeboxBE(BlockPos pPos, BlockState pBlockState) {
        super(BlockEntityRegistry.TIER2_ADVANCED_JUKEBOX.get(), pPos, pBlockState);
    }

    @Override
    public int getContainerSize(){
        return 10;
    }

    @Override
    public ResourceLocation getGuiBackground() {
        return new ResourceLocation(RSJukeboxes.MOD_ID, "textures/gui/jukebox_tier_2_gui.png");
    }
}
