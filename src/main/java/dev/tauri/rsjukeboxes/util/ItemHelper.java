package dev.tauri.rsjukeboxes.util;

import com.mojang.realmsclient.gui.ChatFormatting;
import dev.tauri.rsjukeboxes.RSJukeboxes;
import net.minecraft.client.util.ITooltipFlag;
import org.lwjgl.input.Keyboard;

import javax.annotation.Nullable;
import java.util.Collections;
import java.util.List;

@SuppressWarnings("unused")
public class ItemHelper {
    public interface TooltipFunction {
        List<String> run();
    }

    public static void applyGenericToolTip(String itemName, List<String> components, ITooltipFlag tooltipFlag) {
        applyToolTip(
                Collections.singletonList(ChatFormatting.GRAY + I18n.format("item." + RSJukeboxes.MOD_ID + "." + itemName + ".tooltip")),
                I18n.getAdvancedTooltip("item." + RSJukeboxes.MOD_ID + "." + itemName + ".tooltip.extended", (i, line) -> ChatFormatting.GRAY + line),
                components, tooltipFlag
        );
    }

    public static void applyToolTip(@Nullable List<String> tooltip, @Nullable I18n.AdvancedTooltip tooltipAdvanced, List<String> components, ITooltipFlag tooltipFlag) {
        if (tooltip == null) return;
        int key = Keyboard.KEY_LSHIFT;
        components.addAll(tooltip);
        boolean isKeyDown = Keyboard.isKeyDown(key);
        if ((isKeyDown || tooltipFlag.isAdvanced()) && tooltipAdvanced != null && tooltipAdvanced.formatLines() != null) {
            int width = tooltipAdvanced.getWidth() + 2;
            StringBuilder spaces = new StringBuilder();
            for (int i = 0; i < width; i++) {
                spaces.append(" ");
            }
            components.add(ChatFormatting.DARK_GRAY.toString() + ChatFormatting.STRIKETHROUGH + spaces);
            components.addAll(tooltipAdvanced.formatLines());
            components.add(ChatFormatting.DARK_GRAY.toString() + ChatFormatting.STRIKETHROUGH + spaces);
        } else if (tooltipAdvanced != null && tooltipAdvanced.formatLines() != null) {
            String text = I18n.format("tooltip.general.hold_shift");
            text = text.replaceAll("%key%", Keyboard.getKeyName(key));
            components.add(ChatFormatting.DARK_GRAY.toString() + ChatFormatting.ITALIC + text);
        }
    }
}
