package dev.tauri.rsjukeboxes.util;


import net.minecraft.client.MinecraftClient;
import net.minecraft.text.MutableText;
import net.minecraft.text.Text;

import java.util.ArrayList;
import java.util.List;

public class I18n {
    public static String format(String s) {
        return Text.translatable(s).getString();
    }

    public interface ILineFormat {
        MutableText apply(int lineNumber, MutableText component);
    }

    public static class AdvancedTooltip {
        public String key;
        public ILineFormat lineFormatting;

        public AdvancedTooltip(String key, ILineFormat lineFormatting) {
            this.key = key;
            this.lineFormatting = lineFormatting;
        }

        public int getWidth() {
            List<Text> l = formatLines();
            int textWidth = 0;
            for (Text c : l) {
                int ii = MinecraftClient.getInstance().textRenderer.getWidth(c);
                if (ii > textWidth) {
                    textWidth = ii;
                }
            }
            int spaceWidth = MinecraftClient.getInstance().textRenderer.getWidth(" ");
            return (int) Math.ceil((double) textWidth / (double) spaceWidth);
        }

        public List<Text> formatLines() {
            String text = format(key);
            String[] lines = text.split("%nl%");
            List<Text> linesC = new ArrayList<>();
            int i = 0;
            for (String line : lines)
                linesC.add(lineFormatting.apply(++i, Text.literal(" " + line)));
            if (lines.length > 0 && lines[0].equals(key)) return null;
            return linesC;
        }
    }


    public static AdvancedTooltip getAdvancedTooltip(String s, ILineFormat lineFormat) {
        return new AdvancedTooltip(s, lineFormat);
    }
}
