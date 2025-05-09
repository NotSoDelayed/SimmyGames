package me.notsodelayed.simmygameapi.util;

import java.util.HashMap;
import java.util.Locale;
import java.util.Map;
import java.util.UUID;

import org.apache.commons.lang3.StringUtils;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

public class StringUtil {

    /**
     * Uses {@link ChatColor} to format the provided text.
     * @param text the text
     * @return the colored text
     */
    @SuppressWarnings("deprecation")
    public static String color(String text) {
        return ChatColor.translateAlternateColorCodes('&', text);
    }

    public static String decimal(float number, int decimalPoint) {
        return String.format(("%." + decimalPoint + "f"), number);
    }

    private static final Map<String, String> ALPHABETS = new HashMap<>();
    public static String smallText(String text) {
        if (ALPHABETS.isEmpty()) {
            String[] beg = "ABCDEFGHIJKLMNOPQRSTUVWXYZ".split("");
            String[] smol = "ᴀʙᴄᴅᴇғɢʜɪᴊᴋʟᴍɴᴏᴘᴏʀsᴛᴜᴠᴡxʏᴢ".split("");
            for (int i = 0; i < 26; i++)
                ALPHABETS.put(beg[i], smol[i]);
        }
        StringBuilder output = new StringBuilder();
        for (String s : text.split(""))
            output.append(ALPHABETS.getOrDefault(s.toUpperCase(Locale.ENGLISH), s));
        return output.toString();
    }

    public static String materialName(Material type) {
        return StringUtils.capitalize(type.getKey().getKey().toLowerCase(Locale.ENGLISH).replace("_", " "));
    }

    public static String getDisplayUuid(UUID uuid) {
        return uuid.toString().split("-")[0];
    }

    public static String senderName(@NotNull CommandSender sender) {
        return sender instanceof Player player ? player.getName() : "Console";
    }

}
