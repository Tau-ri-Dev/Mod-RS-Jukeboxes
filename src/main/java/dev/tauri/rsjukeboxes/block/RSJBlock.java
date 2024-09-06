package dev.tauri.rsjukeboxes.block;

import net.minecraft.block.state.IBlockState;
import net.minecraft.client.renderer.tileentity.TileEntitySpecialRenderer;
import net.minecraft.tileentity.TileEntity;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

public interface RSJBlock {

    /**
     * Get class of tile entity - used for registry in client proxy
     *
     * @return class of tile entity
     */
    default Class<? extends TileEntity> getTileEntityClass() {
        return null;
    }

    /**
     * Get TESR of tile entity to register in client proxy
     *
     * @return TESR
     */
    @SideOnly(Side.CLIENT)
    default TileEntitySpecialRenderer<? extends TileEntity> getTESR() {
        return null;
    }

    /**
     * Should block have highlighting edges?
     *
     * @param blockState - current block state
     * @return - should render highlights
     */
    default boolean renderHighlight(IBlockState blockState) {
        return true;
    }
}
