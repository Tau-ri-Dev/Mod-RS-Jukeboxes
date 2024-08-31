package dev.tauri.rsjukeboxes.util;

import com.mojang.blaze3d.platform.InputConstants;
import dev.tauri.rsjukeboxes.RSJukeboxes;
import net.minecraft.ChatFormatting;
import net.minecraft.client.Minecraft;
import net.minecraft.network.chat.Component;
import net.minecraft.world.item.TooltipFlag;

import javax.annotation.Nullable;
import java.util.List;

@SuppressWarnings("unused")
public class ItemHelper {
    public interface TooltipFunction {
        List<Component> run();
    }

    public static void applyGenericToolTip(String itemName, List<Component> components, TooltipFlag tooltipFlag) {
        applyToolTip(
                List.of(Component.translatable("item." + RSJukeboxes.MOD_ID + "." + itemName + ".tooltip").withStyle(ChatFormatting.GRAY)),
                I18n.getAdvancedTooltip("item." + RSJukeboxes.MOD_ID + "." + itemName + ".tooltip.extended", (i, line) -> line.withStyle(ChatFormatting.GRAY)),
                components, tooltipFlag
        );
    }

    public static void applyToolTip(@Nullable List<Component> tooltip, @Nullable I18n.AdvancedTooltip tooltipAdvanced, List<Component> components, TooltipFlag tooltipFlag) {
        if (tooltip == null) return;
        int key = InputConstants.KEY_LSHIFT;
        components.addAll(tooltip);
        boolean isKeyDown = InputConstants.isKeyDown(Minecraft.getInstance().getWindow().getWindow(), key);
        if ((isKeyDown || tooltipFlag.isAdvanced()) && tooltipAdvanced != null && tooltipAdvanced.formatLines() != null) {
            int width = tooltipAdvanced.getWidth() + 2;
            components.add(Component.literal(" ".repeat(width)).withStyle(ChatFormatting.DARK_GRAY).withStyle(ChatFormatting.STRIKETHROUGH));
            components.addAll(tooltipAdvanced.formatLines());
            components.add(Component.literal(" ".repeat(width)).withStyle(ChatFormatting.DARK_GRAY).withStyle(ChatFormatting.STRIKETHROUGH));
        } else if (tooltipAdvanced != null && tooltipAdvanced.formatLines() != null) {
            String text = Component.translatable("tooltip.general.hold_shift").getString();
            text = text.replaceAll("%key%", InputConstants.Type.KEYSYM.getOrCreate(key).getDisplayName().getString());
            components.add(Component.literal(text).withStyle(ChatFormatting.DARK_GRAY).withStyle(ChatFormatting.ITALIC));
        }
    }
}
