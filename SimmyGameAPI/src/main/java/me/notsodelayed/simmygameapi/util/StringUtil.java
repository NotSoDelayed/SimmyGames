package me.notsodelayed.simmygameapi.util;

import java.util.UUID;

import net.kyori.adventure.text.Component;
import org.bukkit.ChatColor;

public class StringUtil {

    /**
     * Uses {@link ChatColor} to format the provided text.
     * @param text the text
     * @return the colored text
     */
    public static String color(String text) {
        return ChatColor.translateAlternateColorCodes('&', text);
    }

    public static Component colorToComponent(String input) {
        StringBuilder result = new StringBuilder();

        boolean checkColor = false;
        for (char c : input.toCharArray()) {
            if (c == '§') {
                checkColor = true;
                continue;
            }
            if (checkColor) {
                ChatColor color = ChatColor.getByChar(c);
                if (color != null) {
                    switch (color) {
                        case BLACK, MAGIC -> result.append("&#000000");
                        case DARK_BLUE -> result.append("&#0000AA");
                        case DARK_GREEN -> result.append("&#00AA00");
                        case DARK_AQUA -> result.append("&#00AAAA");
                        case DARK_RED -> result.append("&#AA0000");
                        case DARK_PURPLE -> result.append("&#AA00AA");
                        case GOLD -> result.append("&#FFAA00");
                        case GRAY -> result.append("&#AAAAAA");
                        case DARK_GRAY -> result.append("&#555555");
                        case BLUE -> result.append("&#5555FF");
                        case GREEN -> result.append("&#55FF55");
                        case AQUA -> result.append("&#55FFFF");
                        case RED -> result.append("&#FF5555");
                        case LIGHT_PURPLE -> result.append("&#FF55FF");
                        case YELLOW -> result.append("&#FFFF55");
                        case RESET, WHITE -> result.append("&#FFFFFF");
                        case BOLD -> result.append("**");
                        case STRIKETHROUGH -> result.append("~~");
                        case UNDERLINE -> result.append("__");
                        case ITALIC -> result.append("*");
                    }
                } else {
                    result.append(c);
                }
            } else {
                result.append(c);
            }
            checkColor = false;
        }
        return Component.text(result.toString());
    }

    public static String getDisplayUuid(UUID uuid) {
        return uuid.toString().split("-")[0];
    }

}
