package me.notsodelayed.simmygameapi.util;

import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import net.kyori.adventure.text.format.TextColor;
import org.jetbrains.annotations.NotNull;

public class ComponentUtil {

    public static Component errorMessage(String text, @NotNull TextColor color) {
        return Component.text(Symbol.X, NamedTextColor.DARK_RED)
                .appendSpace()
                .append(Component.text(text, color));
    }

    public static Component errorMessage(String text) {
        return errorMessage(text, NamedTextColor.WHITE);
    }

    public static Component infoMessage(String text, @NotNull TextColor color) {
        return Component.text(Symbol.INFORMATION, NamedTextColor.GOLD)
                .appendSpace()
                .append(Component.text(text, color));
    }

    public static Component infoMessage(String text) {
        return infoMessage(text, NamedTextColor.WHITE);
    }

    public static Component successMessage(String text, @NotNull TextColor color) {
        return Component.text(Symbol.TICK, NamedTextColor.GREEN)
                .appendSpace()
                .append(Component.text(text, color));
    }

    public static Component successMessage(String text) {
        return successMessage(text, NamedTextColor.WHITE);
    }

}
