package dev.tauri.rsjukeboxes.screen.container;

import com.mojang.blaze3d.systems.RenderSystem;
import dev.tauri.rsjukeboxes.RSJukeboxes;
import dev.tauri.rsjukeboxes.packet.RSJPacketHandlerClient;
import dev.tauri.rsjukeboxes.packet.packets.JukeboxActionPacketToServer;
import dev.tauri.rsjukeboxes.screen.element.IconButton;
import dev.tauri.rsjukeboxes.screen.util.GuiHelper;
import dev.tauri.rsjukeboxes.util.I18n;
import it.unimi.dsi.fastutil.Pair;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.gui.screen.ingame.HandledScreen;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.item.Item;
import net.minecraft.item.MusicDiscItem;
import net.minecraft.text.Text;
import net.minecraft.util.Formatting;
import net.minecraft.util.Identifier;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.List;

import static dev.tauri.rsjukeboxes.screen.util.GuiHelper.*;

public class TieredJukeboxGui extends HandledScreen<TieredJukeboxContainer> {

    public static final Identifier BUTTONS_TEXTURE = new Identifier(RSJukeboxes.MOD_ID, "textures/gui/buttons.png");

    public final List<IconButton> buttons = new ArrayList<>();

    public TieredJukeboxGui(TieredJukeboxContainer container, PlayerInventory pPlayerInventory, Text pTitle) {
        super(container, pPlayerInventory, pTitle);
        this.backgroundWidth = 175;
        this.backgroundHeight = 147 - 18 + ((int) Math.ceil(container.jukebox.getContainerSize() / 5f) * 18);
    }


    @Override
    public void init() {
        super.init();
        buttons.clear();

        var y = (65f - 18f + ((int) Math.ceil(handler.jukebox.getContainerSize() / 5f) * 18)) / 2f;
        buttons.add(new IconButton(0, x + 121, (int) (this.y + y - 24), BUTTONS_TEXTURE, 256, 0, 0, 22, 22, true, I18n.format("gui.jukebox.play")));
        buttons.add(new IconButton(1, x + 147, (int) (this.y + y - 24), BUTTONS_TEXTURE, 256, 0, 44, 22, 22, true, I18n.format("gui.jukebox.stop")));
        buttons.add(new IconButton(2, x + 121, (int) (this.y + y + 2), BUTTONS_TEXTURE, 256, 0, 22, 22, 22, true, I18n.format("gui.jukebox.prev")));
        buttons.add(new IconButton(3, x + 147, (int) (this.y + y + 2), BUTTONS_TEXTURE, 256, 0, 66, 22, 22, true, I18n.format("gui.jukebox.next")));
    }

    @Override
    public void render(@NotNull DrawContext graphics, int mouseX, int mouseY, float partialTicks) {
        GuiHelper.graphics = graphics;
        RenderSystem.disableDepthTest();
        GuiHelper.renderTransparentBackground(graphics, this);

        super.render(graphics, mouseX, mouseY, partialTicks);
        drawMouseoverTooltip(graphics, mouseX, mouseY);
    }

    protected Pair<Long, Long> recordTime = Pair.of(0L, 0L);

    @Override
    protected void drawBackground(@NotNull DrawContext graphics, float partialTick, int mouseX, int mouseY) {
        graphics.getMatrices().push();
        RenderSystem.enableBlend();
        recordTime = Pair.of(0L, 0L);
        RenderSystem.setShaderTexture(0, handler.jukebox.getGuiBackground());
        RenderSystem.setShaderColor(1, 1, 1, 1);
        drawModalRectWithCustomSizedTexture(this.x, this.y, 0, 0, backgroundWidth, backgroundHeight, 256, 256);

        var progressBarY = ((int) Math.ceil(handler.jukebox.getContainerSize() / 5f) * 18) + 44 - 18 + 1 + this.y;
        var progressBarV = ((int) Math.ceil(handler.jukebox.getContainerSize() / 5f) * 18) + 148 - 18;
        var progressBarX = 8 + this.x;
        float progressBarWidth = 0f;
        if (handler.jukebox.getWorld() != null && handler.jukebox.getRendererState().playing) {
            if (Item.byRawId(handler.jukebox.getRendererState().discItemId) instanceof MusicDiscItem record) {
                var progress = (float) (handler.jukebox.getWorld().getTime() - handler.jukebox.getRendererState().playingStarted);
                var length = (float) record.getSongLengthInTicks();
                if (length != 0) {
                    progressBarWidth = (progress / length);
                    if (progressBarWidth > 1) progressBarWidth = 1;
                    if (progressBarWidth < 0) progressBarWidth = 0;
                    recordTime = Pair.of((long) progress, (long) record.getSongLengthInTicks());
                }
            }
        }
        drawModalRectWithCustomSizedTexture(progressBarX, progressBarY, 0, progressBarV, (int) (progressBarWidth * 88), 3, 256, 256);

        for (var btn : buttons) {
            btn.drawButton(graphics, mouseX, mouseY);
        }
        for (var slot : handler.slots) {
            if (slot.id == handler.jukebox.getRendererState().selectedSlot) {
                RenderSystem.setShaderTexture(0, BUTTONS_TEXTURE);
                RenderSystem.setShaderColor(1, 1, 1, 1);
                drawModalRectWithCustomSizedTexture(this.x + slot.x - 2, this.y + slot.y - 2, 0, 88, 20, 20, 256, 256);
                break;
            }
        }
        RenderSystem.disableBlend();
        graphics.getMatrices().pop();
    }


    @Override
    protected void drawForeground(@NotNull DrawContext graphics, int mouseX, int mouseY) {
        RenderSystem.disableDepthTest();
        graphics.drawText(textRenderer, I18n.format("gui.jukebox.playlist"), 8, 10, 4210752, false);
        graphics.drawText(textRenderer, I18n.format("container.inventory"), 8, backgroundHeight - 96 + 2, 4210752, false);

        var progressBarY = ((int) Math.ceil(handler.jukebox.getContainerSize() / 5f) * 18) + 44 - 18;
        var progressBarX = 7;
        if (isPointInRegion(progressBarX, progressBarY, 90, 5, mouseX - this.x, mouseY - this.y)) {
            var secondsPlayed = recordTime.first() / 20;
            var secondsTotal = recordTime.second() / 20;
            var sec = (int) (secondsPlayed % 60);
            var timePlayed = (int) (secondsPlayed / 60) + ":" + (sec < 10 ? "0" + sec : sec);
            sec = (int) (secondsTotal % 60);
            var timeTotal = (int) (secondsTotal / 60) + ":" + (sec < 10 ? "0" + sec : sec);
            List<String> power = List.of(Formatting.WHITE + timePlayed + " / " + timeTotal);
            drawHoveringText(graphics, textRenderer, power, mouseX - this.x, mouseY - this.y);
        }
    }

    @Override
    protected void drawMouseoverTooltip(@NotNull DrawContext graphics, int mouseX, int mouseY) {
        super.drawMouseoverTooltip(graphics, mouseX, mouseY);
        graphics.getMatrices().push();
        for (var btn : buttons) {
            btn.drawFg(mouseX, mouseY);
        }
        graphics.getMatrices().pop();
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
        RSJPacketHandlerClient.sendToServer(new JukeboxActionPacketToServer(handler.jukebox.getPos(), action));
    }
}
