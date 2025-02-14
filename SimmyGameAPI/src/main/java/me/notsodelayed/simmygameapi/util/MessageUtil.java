package me.notsodelayed.simmygameapi.util;

import java.util.Arrays;

import org.bukkit.command.CommandSender;
import org.bukkit.command.ConsoleCommandSender;
import org.jetbrains.annotations.NotNull;

/**
 * Utility for handing messages.
 */
public class MessageUtil {

    /**
     * Sends formatted messages to a recipient.
     * @param recipient the message recipient
     * @param messages the messages
     */
    public static void sendMessage(@NotNull CommandSender recipient, @NotNull String... messages) {
        for (String message : messages)
            recipient.sendMessage(StringUtil.color(message));
    }

    /**
     * Sends error messages to a recipient.
     * @param recipient the message recipient
     * @param messages the messages
     */
    public static void sendErrorMessage(@NotNull CommandSender recipient, @NotNull String... messages) {
        for (String message : messages)
            recipient.sendMessage(StringUtil.color("&4" + Symbol.X + " " + message));
    }

    /**
     * @param messages the messages
     * @return an array of error formatted message
     */
    public static String[] errorMessage(@NotNull String... messages) {
        return Arrays.stream(messages).
                map(message -> StringUtil.color("&4" + Symbol.X + " " + message))
                .toArray(String[]::new);
    }

    /**
     * Sends info messages to a recipient.
     * @param recipient the message recipient
     * @param messages the messages
     */
    public static void sendInfoMessage(@NotNull CommandSender recipient, @NotNull String... messages) {
        for (String message : messages)
            recipient.sendMessage(StringUtil.color("&6" + Symbol.INFORMATION + "&e " + message));
    }

    /**
     * @param messages the messages
     * @return an array of info formatted message
     */
    public static String[] infoMessage(@NotNull String... messages) {
        return Arrays.stream(messages).
                map(message -> StringUtil.color("&6" + Symbol.INFORMATION + "&e " + message))
                .toArray(String[]::new);
    }

    /**
     * Sends success messages to a recipient.
     * @param recipient the message recipient
     * @param messages the messages
     */
    public static void sendSuccessMessage(@NotNull CommandSender recipient, @NotNull String... messages) {
        for (String message : messages)
            recipient.sendMessage(StringUtil.color("&2" + Symbol.TICK + "&a " + message));
    }

    /**
     * Sends a formatted message to a recipient, in condition to if they are OP, they have the specified permission, or it's the {@link ConsoleCommandSender}.
     * @param recipient the message recipient
     * @param permission the permission of the message
     * @param message the message
     */
    public static void sendPermissionMessage(@NotNull CommandSender recipient, @NotNull String permission, String message) {
        if (recipient.isOp() || recipient.hasPermission(permission) || recipient instanceof ConsoleCommandSender)
            recipient.sendMessage(StringUtil.color(message));
    }

    public static void playerOnly(CommandSender recipient) {
        sendErrorMessage(recipient, "&cThis command is only for players.");
    }

}
