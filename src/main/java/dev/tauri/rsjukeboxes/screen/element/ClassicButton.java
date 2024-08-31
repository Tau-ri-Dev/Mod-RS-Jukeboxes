package dev.tauri.rsjukeboxes.screen.element;

import net.minecraft.client.gui.components.Button;
import net.minecraft.network.chat.Component;

public class ClassicButton extends Button {
    public final int id;
    public ClassicButton(int buttonId, int x, int y, int widthIn, int heightIn, String buttonText) {
        super(Button.builder(Component.literal(buttonText), (btn) -> {}).bounds(x, y, widthIn, heightIn));
        this.id = buttonId;
    }
}
