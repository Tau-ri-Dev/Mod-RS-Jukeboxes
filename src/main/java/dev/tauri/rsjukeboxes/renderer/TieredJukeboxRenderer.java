package dev.tauri.rsjukeboxes.renderer;

import net.minecraft.client.renderer.blockentity.BlockEntityRendererProvider;

public class TieredJukeboxRenderer extends AbstractRSJukeboxRenderer<GenericRSJukeboxRendererState> {
    public TieredJukeboxRenderer(BlockEntityRendererProvider.Context ctx) {
        super(ctx);
    }

    @Override
    public void renderSafe() {
        renderSpinningDisc();
    }
}
