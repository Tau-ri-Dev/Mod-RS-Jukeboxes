package dev.tauri.rsjukeboxes.renderer;

import net.minecraft.client.render.block.entity.BlockEntityRendererFactory;

public class TieredJukeboxRenderer extends AbstractRSJukeboxRenderer<GenericRSJukeboxRendererState> {
    public TieredJukeboxRenderer(BlockEntityRendererFactory.Context ctx) {
        super(ctx);
    }

    @Override
    public void renderSafe() {
        renderSpinningDisc();
    }
}
