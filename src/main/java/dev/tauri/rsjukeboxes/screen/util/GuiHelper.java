package dev.tauri.rsjukeboxes.screen.util;

import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.vertex.*;
import net.minecraft.client.gui.Font;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.client.renderer.GameRenderer;
import net.minecraft.network.chat.Component;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import org.joml.Matrix4f;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@OnlyIn(Dist.CLIENT)
public class GuiHelper {

    public static GuiGraphics graphics = null;

    public static boolean isPointInRegion(int rectX, int rectY, int rectWidth, int rectHeight, int pointX, int pointY) {
        return pointX >= rectX && pointY >= rectY && pointX < (rectX + rectWidth) && pointY < (rectY + rectHeight);
    }

    public static void drawModalRectWithCustomSizedTexture(int x, int y, float u, float v, int width, int height, float textureWidth, float textureHeight) {
        float f = 1.0F / textureWidth;
        float f1 = 1.0F / textureHeight;
        RenderSystem.setShader(GameRenderer::getPositionTexShader);
        Matrix4f matrix = graphics.pose().last().pose();
        Tesselator tessellator = Tesselator.getInstance();
        BufferBuilder bufferbuilder = tessellator.begin(VertexFormat.Mode.QUADS, DefaultVertexFormat.POSITION_TEX);
        bufferbuilder.addVertex(matrix, x, y + height, 0.0f).setUv((u * f), ((v + (float) height) * f1));
        bufferbuilder.addVertex(matrix, (x + width), (y + height), 0.0f).setUv(((u + (float) width) * f), ((v + (float) height) * f1));
        bufferbuilder.addVertex(matrix, (x + width), y, 0.0f).setUv(((u + (float) width) * f), (v * f1));
        bufferbuilder.addVertex(matrix, x, y, 0.0f).setUv((u * f), (v * f1));
        BufferUploader.drawWithShader(bufferbuilder.buildOrThrow());
    }

    public static void drawHoveringText(GuiGraphics graphics, Font font, List<String> textLines, int mouseX, int mouseY) {
        List<Component> components = new ArrayList<>();
        for (String s : textLines)
            components.add(Component.literal(s));
        graphics.renderTooltip(font, components, Optional.empty(), mouseX, mouseY);
    }

    public static void renderTransparentBackground(GuiGraphics graphics, Screen screen) {
        graphics.fillGradient(0, 0, screen.width, screen.height, -1072689136, -804253680);
    }
}
