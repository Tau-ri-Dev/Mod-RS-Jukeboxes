package dev.tauri.rsjukeboxes.screen.element;

import com.mojang.blaze3d.systems.RenderSystem;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import static dev.tauri.rsjukeboxes.screen.util.GuiHelper.drawModalRectWithCustomSizedTexture;
import static dev.tauri.rsjukeboxes.screen.util.GuiHelper.isPointInRegion;

public class IconButton extends ClassicButton {
    public GuiGraphics graphics;
    public String[] label;
    public ResourceLocation texture;
    public final int u;
    public final int v;
    public final int width;
    public final int height;
    public boolean enableHover;
    public final int texSize;

    public IconButton(int buttonId, int x, int y, ResourceLocation texture, int texSize, int u, int v, int width, int height, boolean enableHover, String... label) {
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

    public void drawButton(GuiGraphics graphics, int mouseX, int mouseY) {
        this.graphics = graphics;
        if (this.visible) {
            this.isHovered = isPointInRegion(getX(), getY(), width, height, mouseX, mouseY);
            RenderSystem.setShaderTexture(0, texture);
            if (enableHover && isHovered && isActive()) {
                drawModalRectWithCustomSizedTexture(getX(), getY(), u + width, v, width, height, texSize, texSize);
            } else {
                drawModalRectWithCustomSizedTexture(getX(), getY(), u, v, width, height, texSize, texSize);
            }
        }
    }

    public void drawFg(int mouseX, int mouseY) {
        if (isHovered) {
            List<Component> c = new ArrayList<>();
            List.of(label).forEach((e) -> c.add(Component.literal(e)));
            graphics.renderTooltip(Minecraft.getInstance().font, c, Optional.empty(), mouseX, mouseY);
        }
    }
}
