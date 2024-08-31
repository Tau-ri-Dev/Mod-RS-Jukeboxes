package dev.tauri.rsjukeboxes.screen.container;

import com.mojang.blaze3d.systems.RenderSystem;
import dev.tauri.rsjukeboxes.RSJukeboxes;
import dev.tauri.rsjukeboxes.packet.RSJPacketHandler;
import dev.tauri.rsjukeboxes.packet.packets.JukeboxActionPacketToServer;
import dev.tauri.rsjukeboxes.screen.element.IconButton;
import dev.tauri.rsjukeboxes.screen.util.GuiHelper;
import dev.tauri.rsjukeboxes.util.I18n;
import it.unimi.dsi.fastutil.Pair;
import net.minecraft.ChatFormatting;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.screens.inventory.AbstractContainerScreen;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.RecordItem;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.List;

import static dev.tauri.rsjukeboxes.screen.util.GuiHelper.*;

public class TieredJukeboxGui extends AbstractContainerScreen<TieredJukeboxContainer> {

    public static final ResourceLocation BUTTONS_TEXTURE = new ResourceLocation(RSJukeboxes.MOD_ID, "textures/gui/buttons.png");

    public final List<IconButton> buttons = new ArrayList<>();

    public TieredJukeboxGui(TieredJukeboxContainer container, Inventory pPlayerInventory, Component pTitle) {
        super(container, pPlayerInventory, pTitle);
        this.imageWidth = 175;
        this.imageHeight = 147 - 18 + ((int) Math.ceil(container.jukebox.getContainerSize() / 5f) * 18);
    }


    @Override
    public void init() {
        super.init();
        buttons.clear();

        var y = (65f - 18f + ((int) Math.ceil(menu.jukebox.getContainerSize() / 5f) * 18)) / 2f;
        buttons.add(new IconButton(0, getGuiLeft() + 121, (int) (getGuiTop() + y - 24), BUTTONS_TEXTURE, 256, 0, 0, 22, 22, true, "Play"));
        buttons.add(new IconButton(1, getGuiLeft() + 147, (int) (getGuiTop() + y - 24), BUTTONS_TEXTURE, 256, 0, 44, 22, 22, true, "Stop"));
        buttons.add(new IconButton(2, getGuiLeft() + 121, (int) (getGuiTop() + y + 2), BUTTONS_TEXTURE, 256, 0, 22, 22, 22, true, "Previous"));
        buttons.add(new IconButton(3, getGuiLeft() + 147, (int) (getGuiTop() + y + 2), BUTTONS_TEXTURE, 256, 0, 66, 22, 22, true, "Next"));
    }

    @Override
    public void render(@NotNull GuiGraphics graphics, int mouseX, int mouseY, float partialTicks) {
        GuiHelper.graphics = graphics;
        RenderSystem.disableDepthTest();
        renderTransparentBackground(graphics, this);

        super.render(graphics, mouseX, mouseY, partialTicks);
        renderTooltip(graphics, mouseX, mouseY);
    }

    protected Pair<Long, Long> recordTime = Pair.of(0L, 0L);

    @Override
    protected void renderBg(@NotNull GuiGraphics graphics, float partialTick, int mouseX, int mouseY) {
        graphics.pose().pushPose();
        RenderSystem.enableBlend();
        recordTime = Pair.of(0L, 0L);
        RenderSystem.setShaderTexture(0, menu.jukebox.getGuiBackground());
        RenderSystem.setShaderColor(1, 1, 1, 1);
        drawModalRectWithCustomSizedTexture(leftPos, topPos, 0, 0, imageWidth, imageHeight, 256, 256);

        var progressBarY = ((int) Math.ceil(menu.jukebox.getContainerSize() / 5f) * 18) + 44 - 18 + 1 + topPos;
        var progressBarV = ((int) Math.ceil(menu.jukebox.getContainerSize() / 5f) * 18) + 148 - 18;
        var progressBarX = 8 + leftPos;
        float progressBarWidth = 0f;
        if (menu.jukebox.getLevel() != null && menu.jukebox.getRendererState().playing) {
            if (Item.byId(menu.jukebox.getRendererState().discItemId) instanceof RecordItem record) {
                var progress = (float) (menu.jukebox.getLevel().getGameTime() - menu.jukebox.getRendererState().playingStarted);
                var length = (float) record.getLengthInTicks();
                if (length != 0) {
                    progressBarWidth = (progress / length);
                    if (progressBarWidth > 1) progressBarWidth = 1;
                    if (progressBarWidth < 0) progressBarWidth = 0;
                    recordTime = Pair.of((long) progress, (long) record.getLengthInTicks());
                }
            }
        }
        drawModalRectWithCustomSizedTexture(progressBarX, progressBarY, 0, progressBarV, (int) (progressBarWidth * 88), 3, 256, 256);

        for (var btn : buttons) {
            btn.drawButton(graphics, mouseX, mouseY);
        }
        for (var slot : menu.slots) {
            if (slot.index == menu.jukebox.getRendererState().selectedSlot) {
                RenderSystem.setShaderTexture(0, BUTTONS_TEXTURE);
                RenderSystem.setShaderColor(1, 1, 1, 1);
                drawModalRectWithCustomSizedTexture(leftPos + slot.x - 2, topPos + slot.y - 2, 0, 88, 20, 20, 256, 256);
                break;
            }
        }
        RenderSystem.disableBlend();
        graphics.pose().popPose();
    }


    @Override
    protected void renderLabels(@NotNull GuiGraphics graphics, int mouseX, int mouseY) {
        RenderSystem.disableDepthTest();
        graphics.drawString(font, I18n.format("gui.jukebox.playlist"), 8, 10, 4210752, false);
        graphics.drawString(font, I18n.format("container.inventory"), 8, imageHeight - 96 + 2, 4210752, false);

        var progressBarY = ((int) Math.ceil(menu.jukebox.getContainerSize() / 5f) * 18) + 44 - 18;
        var progressBarX = 7;
        if (isPointInRegion(progressBarX, progressBarY, 90, 5, mouseX - getGuiLeft(), mouseY - getGuiTop())) {
            var secondsPlayed = recordTime.first() / 20;
            var secondsTotal = recordTime.second() / 20;
            var sec = (int) (secondsPlayed % 60);
            var timePlayed = (int) (secondsPlayed / 60) + ":" + (sec < 10 ? "0" + sec : sec);
            sec = (int) (secondsTotal % 60);
            var timeTotal = (int) (secondsTotal / 60) + ":" + (sec < 10 ? "0" + sec : sec);
            List<String> power = List.of(ChatFormatting.WHITE + timePlayed + " / " + timeTotal);
            drawHoveringText(graphics, font, power, mouseX - getGuiLeft(), mouseY - getGuiTop());
        }
    }

    @Override
    protected void renderTooltip(@NotNull GuiGraphics graphics, int mouseX, int mouseY) {
        super.renderTooltip(graphics, mouseX, mouseY);
        graphics.pose().pushPose();
        for (var btn : buttons) {
            btn.drawFg(mouseX, mouseY);
        }
        graphics.pose().popPose();
    }


    @Override
    public boolean mouseClicked(double mouseX, double mouseY, int mouseButton) {
        for (var btn : buttons) {
            if (btn.mouseClicked(mouseX, mouseY, mouseButton)) {
                switch (btn.id) {
                    case 0:
                        sendAction(JukeboxActionPacketToServer.JukeboxAction.PLAY);
                        break;
                    case 1:
                        sendAction(JukeboxActionPacketToServer.JukeboxAction.STOP);
                        break;
                    case 2:
                        sendAction(JukeboxActionPacketToServer.JukeboxAction.PREVIOUS);
                        break;
                    case 3:
                        sendAction(JukeboxActionPacketToServer.JukeboxAction.NEXT);
                        break;
                    default:
                        break;
                }
                return true;
            }
        }
        return super.mouseClicked(mouseX, mouseY, mouseButton);
    }

    private void sendAction(@NotNull JukeboxActionPacketToServer.JukeboxAction action) {
        RSJPacketHandler.sendToServer(new JukeboxActionPacketToServer(menu.jukebox.getBlockPos(), action));
    }
}
