package dev.tauri.rsjukeboxes.screen.element;

import com.mojang.blaze3d.systems.RenderSystem;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.text.Text;
import net.minecraft.util.Identifier;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import static dev.tauri.rsjukeboxes.screen.util.GuiHelper.drawModalRectWithCustomSizedTexture;
import static dev.tauri.rsjukeboxes.screen.util.GuiHelper.isPointInRegion;

public class IconButton extends ClassicButton {
    public DrawContext graphics;
    public String[] label;
    public Identifier texture;
    public final int u;
    public final int v;
    public final int width;
    public final int height;
    public boolean enableHover;
    public final int texSize;

    public IconButton(int buttonId, int x, int y, Identifier texture, int texSize, int u, int v, int width, int height, boolean enableHover, String... label) {
        super(buttonId, x, y, width, height, "");
        this.label = label;
        this.texture = texture;
        this.u = u;
        this.v = v;
        this.width = width;
        this.height = height;
        this.texSize = texSize;
        this.enableHover = enableHover;
    }

    public void drawButton(DrawContext graphics, int mouseX, int mouseY) {
        this.graphics = graphics;
        if (this.visible) {
            this.hovered = isPointInRegion(getX(), getY(), width, height, mouseX, mouseY);
            RenderSystem.setShaderTexture(0, texture);
            if (enableHover && hovered && active) {
                drawModalRectWithCustomSizedTexture(getX(), getY(), u + width, v, width, height, texSize, texSize);
            } else {
                drawModalRectWithCustomSizedTexture(getX(), getY(), u, v, width, height, texSize, texSize);
            }
        }
    }

    public void drawFg(int mouseX, int mouseY) {
        if (hovered) {
            List<Text> c = new ArrayList<>();
            List.of(label).forEach((e) -> c.add(Text.literal(e)));
            graphics.drawTooltip(MinecraftClient.getInstance().textRenderer, c, Optional.empty(), mouseX, mouseY);
        }
    }
}
