package dev.tauri.rsjukeboxes.util;

import net.minecraft.client.Minecraft;

import java.util.ArrayList;
import java.util.List;

public class I18n {
    public static String format(String s) {
        return net.minecraft.client.resources.I18n.format(s);
    }

    public interface ILineFormat {
        String apply(int lineNumber, String component);
    }

    public static class AdvancedTooltip {
        public String key;
        public ILineFormat lineFormatting;

        public AdvancedTooltip(String key, ILineFormat lineFormatting) {
            this.key = key;
            this.lineFormatting = lineFormatting;
        }

        public int getWidth() {
            List<String> l = formatLines();
            int textWidth = 0;
            for (String c : l) {
                int ii = Minecraft.getMinecraft().fontRenderer.getStringWidth(c);
                if (ii > textWidth) {
                    textWidth = ii;
                }
            }
            int spaceWidth = Minecraft.getMinecraft().fontRenderer.getStringWidth(" ");
            return (int) Math.ceil((double) textWidth / (double) spaceWidth);
        }

        public List<String> formatLines() {
            String text = format(key);
            String[] lines = text.split("%nl%");
            List<String> linesC = new ArrayList<>();
            int i = 0;
            for (String line : lines)
                linesC.add(lineFormatting.apply(++i, (" " + line)));
            if (lines.length > 0 && lines[0].equals(key)) return null;
            return linesC;
        }
    }


    public static AdvancedTooltip getAdvancedTooltip(String s, ILineFormat lineFormat) {
        return new AdvancedTooltip(s, lineFormat);
    }
}
