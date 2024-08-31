package dev.tauri.rsjukeboxes.renderer;

import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.math.Axis;
import dev.tauri.rsjukeboxes.blockentity.AbstractRSJukeboxBE;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.LevelRenderer;
import net.minecraft.client.renderer.LightTexture;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.blockentity.BlockEntityRenderer;
import net.minecraft.client.renderer.blockentity.BlockEntityRendererProvider;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemDisplayContext;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.Level;

import javax.annotation.ParametersAreNonnullByDefault;

public abstract class AbstractRSJukeboxRenderer<S extends GenericRSJukeboxRendererState> implements BlockEntityRenderer<AbstractRSJukeboxBE> {
    public AbstractRSJukeboxRenderer(BlockEntityRendererProvider.Context ignored) {
    }

    protected AbstractRSJukeboxBE jukebox;
    protected float partialTicks;
    protected PoseStack poseStack;
    protected MultiBufferSource bufferSource;
    protected int packedLight;
    protected int packedOverlay;

    protected Level level;
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
            int light = LevelRenderer.getLightColor(level, jukebox.getBlockPos().offset(side.getNormal()));
            blockSum += (LightTexture.block(light) * 2L);
            skySum += LightTexture.sky(light);
        }
        if (count == 0) return LightTexture.FULL_BRIGHT;
        return LightTexture.pack((int) (blockSum / count2), (int) (skySum / count));
    }

    @Override
    @ParametersAreNonnullByDefault
    @SuppressWarnings("unchecked")
    public final void render(AbstractRSJukeboxBE pBlockEntity, float pPartialTick, PoseStack pPoseStack, MultiBufferSource pBuffer, int pPackedLight, int pPackedOverlay) {
        jukebox = pBlockEntity;
        partialTicks = pPartialTick;
        poseStack = pPoseStack;
        bufferSource = pBuffer;
        if (jukebox.getLevel() == null) return;
        level = jukebox.getLevel();
        tick = level.getGameTime();
        pos = jukebox.getBlockPos();
        rendererState = (S) jukebox.getRendererState();
        packedLight = getCombinedLight();
        packedOverlay = pPackedOverlay;
        poseStack.pushPose();
        RenderSystem.enableDepthTest();
        renderSafe();
        RenderSystem.disableDepthTest();
        poseStack.popPose();
    }

    public void renderSpinningDisc() {
        if (!rendererState.discInserted) return;
        var item = Item.byId(rendererState.discItemId);
        if (item == Items.AIR) return;
        poseStack.pushPose();
        RenderSystem.enableBlend();
        RenderSystem.setShaderColor(1, 1, 1, 1);
        poseStack.translate(0.5, 3 / 4f, 0.5);
        poseStack.mulPose(Axis.XP.rotationDegrees(90));
        if (rendererState.playing)
            poseStack.mulPose(Axis.ZP.rotationDegrees((tick - rendererState.playingStarted) / 2f));
        poseStack.pushPose();
        poseStack.scale(0.9f, 1.5f * 0.9f, 0.9f);
        Minecraft.getInstance().getItemRenderer().renderStatic(new ItemStack(item), ItemDisplayContext.FIXED, packedLight, packedOverlay, poseStack, bufferSource, level, 0);
        RenderSystem.disableBlend();
        poseStack.popPose();
        poseStack.popPose();
    }

    public abstract void renderSafe();
}
