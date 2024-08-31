package dev.tauri.rsjukeboxes.renderer;

import net.minecraft.client.renderer.blockentity.BlockEntityRendererProvider;

public class RepeatingJukeboxRenderer extends AbstractRSJukeboxRenderer<GenericRSJukeboxRendererState> {
    public RepeatingJukeboxRenderer(BlockEntityRendererProvider.Context ctx) {
        super(ctx);
    }

    @Override
    public void renderSafe() {
        renderSpinningDisc();
    }
}
