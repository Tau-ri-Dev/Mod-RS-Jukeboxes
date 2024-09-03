package dev.tauri.rsjukeboxes.screen.element;


import net.minecraft.client.gui.widget.ButtonWidget;
import net.minecraft.text.Text;

public class ClassicButton extends ButtonWidget {
    public final int id;
    public ClassicButton(int buttonId, int x, int y, int widthIn, int heightIn, String buttonText) {
        super(x, y, widthIn, heightIn, Text.literal(buttonText), (btn) -> {}, (text) -> Text.literal(buttonText));
        this.id = buttonId;
    }
}
