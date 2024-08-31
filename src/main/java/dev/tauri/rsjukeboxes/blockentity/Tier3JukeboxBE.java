package dev.tauri.rsjukeboxes.blockentity;

import dev.tauri.rsjukeboxes.RSJukeboxes;
import dev.tauri.rsjukeboxes.registry.BlockEntityRegistry;
import net.minecraft.core.BlockPos;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.level.block.state.BlockState;

public class Tier3JukeboxBE extends AbstractTieredJukeboxBE {
    public Tier3JukeboxBE(BlockPos pPos, BlockState pBlockState) {
        super(BlockEntityRegistry.TIER3_ADVANCED_JUKEBOX.get(), pPos, pBlockState);
    }

    @Override
    public int getContainerSize(){
        return 15;
    }

    @Override
    public ResourceLocation getGuiBackground() {
        return new ResourceLocation(RSJukeboxes.MOD_ID, "textures/gui/jukebox_tier_3_gui.png");
    }
}
