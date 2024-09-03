package dev.tauri.rsjukeboxes.util;

import dev.tauri.rsjukeboxes.RSJukeboxes;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.item.TooltipContext;
import net.minecraft.client.util.InputUtil;
import net.minecraft.text.Text;
import net.minecraft.util.Formatting;
import org.jetbrains.annotations.Nullable;

import java.util.List;

import static net.minecraft.util.Formatting.GRAY;

@SuppressWarnings("unused")
public class ItemHelper {
    public interface TooltipFunction {
        List<Text> run();
    }

    public static void applyGenericToolTip(String itemName, List<Text> tooltip, TooltipContext tooltipFlag) {
        applyToolTip(
                List.of(Text.translatable("item." + RSJukeboxes.MOD_ID + "." + itemName + ".tooltip").formatted(GRAY)),
                I18n.getAdvancedTooltip("item." + RSJukeboxes.MOD_ID + "." + itemName + ".tooltip.extended", (i, line) -> line.formatted(GRAY)),
                tooltip, tooltipFlag
        );
    }

    public static void applyToolTip(@Nullable List<Text> tooltip, @Nullable I18n.AdvancedTooltip tooltipAdvanced, List<Text> components, TooltipContext tooltipFlag) {
        if (tooltip == null) return;
        int key = InputUtil.GLFW_KEY_LEFT_SHIFT;
        components.addAll(tooltip);
        boolean isKeyDown = InputUtil.isKeyPressed(MinecraftClient.getInstance().getWindow().getHandle(), key);
        if ((isKeyDown || tooltipFlag.isAdvanced()) && tooltipAdvanced != null && tooltipAdvanced.formatLines() != null) {
            int width = tooltipAdvanced.getWidth() + 2;
            components.add(Text.literal(" ".repeat(width)).formatted(Formatting.DARK_GRAY).formatted(Formatting.STRIKETHROUGH));
            components.addAll(tooltipAdvanced.formatLines());
            components.add(Text.literal(" ".repeat(width)).formatted(Formatting.DARK_GRAY).formatted(Formatting.STRIKETHROUGH));
        } else if (tooltipAdvanced != null && tooltipAdvanced.formatLines() != null) {
            String text = Text.translatable("tooltip.general.hold_shift").getString();
            text = text.replaceAll("%key%", I18n.format(InputUtil.Type.KEYSYM.createFromCode(key).getTranslationKey()));
            components.add(Text.literal(text).formatted(Formatting.DARK_GRAY).formatted(Formatting.ITALIC));
        }
    }
}
