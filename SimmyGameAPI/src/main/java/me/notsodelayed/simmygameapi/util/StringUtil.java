package me.notsodelayed.simmygameapi.util;

import java.util.UUID;

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

    public static String getDisplayUuid(UUID uuid) {
        return uuid.toString().split("-")[0];
    }

}
