package dev.tauri.rsjukeboxes.renderer;

import com.mojang.blaze3d.systems.RenderSystem;
import dev.tauri.rsjukeboxes.blockentity.AbstractRSJukeboxBE;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.render.LightmapTextureManager;
import net.minecraft.client.render.VertexConsumerProvider;
import net.minecraft.client.render.WorldRenderer;
import net.minecraft.client.render.block.entity.BlockEntityRenderer;
import net.minecraft.client.render.block.entity.BlockEntityRendererFactory;
import net.minecraft.client.render.model.json.ModelTransformationMode;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.util.math.RotationAxis;
import net.minecraft.world.World;

public abstract class AbstractRSJukeboxRenderer<S extends GenericRSJukeboxRendererState> implements BlockEntityRenderer<AbstractRSJukeboxBE> {
    public AbstractRSJukeboxRenderer(BlockEntityRendererFactory.Context ignored) {
    }

    protected AbstractRSJukeboxBE jukebox;
    protected float partialTicks;
    protected MatrixStack poseStack;
    protected VertexConsumerProvider bufferSource;
    protected int packedLight;
    protected int packedOverlay;

    protected World level;
    protected BlockPos pos;

    protected S rendererState;

    protected long tick;

    public int getCombinedLight() {
        int count = 0;
        int count2 = 0;
        long blockSum = 0;
        long skySum = 0;
        for (var side : Direction.values()) {
            count++;
            count2 += 2;
            int light = WorldRenderer.getLightmapCoordinates(level, jukebox.getPos().offset(side));
            blockSum += (LightmapTextureManager.getBlockLightCoordinates(light) * 2L);
            skySum += LightmapTextureManager.getSkyLightCoordinates(light);
        }
        if (count == 0) return LightmapTextureManager.MAX_LIGHT_COORDINATE;
        return LightmapTextureManager.pack((int) (blockSum / count2), (int) (skySum / count));
    }

    @Override
    @SuppressWarnings("unchecked")
    public final void render(AbstractRSJukeboxBE pBlockEntity, float pPartialTick, MatrixStack pPoseStack, VertexConsumerProvider pBuffer, int pPackedLight, int pPackedOverlay) {
        jukebox = pBlockEntity;
        partialTicks = pPartialTick;
        poseStack = pPoseStack;
        bufferSource = pBuffer;
        if (jukebox.getWorld() == null) return;
        level = jukebox.getWorld();
        tick = level.getTime();
        pos = jukebox.getPos();
        rendererState = (S) jukebox.getRendererState();
        packedLight = getCombinedLight();
        packedOverlay = pPackedOverlay;
        poseStack.push();
        RenderSystem.enableDepthTest();
        renderSafe();
        RenderSystem.disableDepthTest();
        poseStack.pop();
    }

    public void renderSpinningDisc() {
        if (!rendererState.discInserted) return;
        var item = Item.byRawId(rendererState.discItemId);
        if (item == Items.AIR) return;
        poseStack.push();
        RenderSystem.enableBlend();
        RenderSystem.setShaderColor(1, 1, 1, 1);
        poseStack.translate(0.5, 3 / 4f, 0.5);
        poseStack.multiply(RotationAxis.POSITIVE_X.rotationDegrees(90));
        if (rendererState.playing)
            poseStack.multiply(RotationAxis.POSITIVE_Z.rotationDegrees((tick - rendererState.playingStarted) / 2f));
        poseStack.push();
        poseStack.scale(0.9f, 1.5f * 0.9f, 0.9f);
        MinecraftClient.getInstance().getItemRenderer().renderItem(new ItemStack(item), ModelTransformationMode.FIXED, packedLight, packedOverlay, poseStack, bufferSource, level, 0);
        RenderSystem.disableBlend();
        poseStack.pop();
        poseStack.pop();
    }

    public abstract void renderSafe();
}
