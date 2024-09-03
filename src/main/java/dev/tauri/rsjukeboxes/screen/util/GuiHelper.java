package dev.tauri.rsjukeboxes.screen.util;

import com.mojang.blaze3d.systems.RenderSystem;
import net.minecraft.client.font.TextRenderer;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.render.*;
import net.minecraft.text.Text;
import org.joml.Matrix4f;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public class GuiHelper {

    public static DrawContext graphics = null;

    public static boolean isPointInRegion(int rectX, int rectY, int rectWidth, int rectHeight, int pointX, int pointY) {
        return pointX >= rectX && pointY >= rectY && pointX < (rectX + rectWidth) && pointY < (rectY + rectHeight);
    }

    public static void drawModalRectWithCustomSizedTexture(int x, int y, float u, float v, int width, int height, float textureWidth, float textureHeight) {
        float f = 1.0F / textureWidth;
        float f1 = 1.0F / textureHeight;
        RenderSystem.setShader(GameRenderer::getPositionTexProgram);
        Matrix4f matrix = graphics.getMatrices().peek().getPositionMatrix();
        Tessellator tessellator = Tessellator.getInstance();
        BufferBuilder bufferbuilder = tessellator.getBuffer();
        bufferbuilder.begin(VertexFormat.DrawMode.QUADS, VertexFormats.POSITION_TEXTURE);
        bufferbuilder.vertex(matrix, x, y + height, 0.0f).texture((u * f), ((v + (float) height) * f1)).next();
        bufferbuilder.vertex(matrix, (x + width), (y + height), 0.0f).texture(((u + (float) width) * f), ((v + (float) height) * f1)).next();
        bufferbuilder.vertex(matrix, (x + width), y, 0.0f).texture(((u + (float) width) * f), (v * f1)).next();
        bufferbuilder.vertex(matrix, x, y, 0.0f).texture((u * f), (v * f1)).next();
        BufferRenderer.drawWithGlobalProgram(bufferbuilder.end());
    }

    public static void drawHoveringText(DrawContext graphics, TextRenderer font, List<String> textLines, int mouseX, int mouseY) {
        List<Text> components = new ArrayList<>();
        for (String s : textLines)
            components.add(Text.literal(s));
        graphics.drawTooltip(font, components, Optional.empty(), mouseX, mouseY);
    }

    public static void renderTransparentBackground(DrawContext graphics, Screen screen) {
        graphics.fillGradient(0, 0, screen.width, screen.height, -1072689136, -804253680);
    }
}
