package me.notsodelayed.simmygameapi.util;

import java.util.UUID;

import me.notsodelayed.simmygameapi.api.game.Game;
import org.bukkit.command.CommandSender;
import org.bukkit.command.ConsoleCommandSender;
import org.jetbrains.annotations.Nullable;

public class CommandUtil {

    /**
     * @param sender the sender
     * @return whether the executor is a player
     */
    public static boolean isPlayer(CommandSender sender) {
        if (sender instanceof ConsoleCommandSender) {
            MessageUtil.sendErrorMessage(sender, "This command is for players.");
            return false;
        }
        return true;
    }

    /**
     * @param sender the sender to check
     * @param permission the permission
     * @return whether the executor is a console, or the player has the permission
     */
    public static boolean hasPermission(CommandSender sender, String permission) {
        if (sender instanceof ConsoleCommandSender)
            return true;
        return sender.hasPermission(permission);
    }

    @Nullable
    public static Game parseGame(CommandSender sender, String stringUuid) {
        UUID uuid = null;
        if (stringUuid.length() == 36) {
            try {
                uuid = UUID.fromString(stringUuid);
            } catch (IllegalArgumentException ex) {
                MessageUtil.sendInfoMessage(sender, String.format("Invalid UUID '%s': Please input a full UUID, or the first portion of a UUID", stringUuid));
                return null;
            }
        } else if (stringUuid.length() == 8) {
            for (UUID u : Game.getGames().keySet()) {
                if (!u.toString().split("-")[0].equals(stringUuid))
                    continue;
                uuid = u;
            }
        }
        if (uuid == null) {
            MessageUtil.sendErrorMessage(sender, String.format("Unable to parse a UUID from '%s'.", stringUuid));
            return null;
        }
        Game game;
        for (UUID u : Game.getGames().keySet()) {
            if (!uuid.equals(u))
                continue;
            game = Game.getGames().get(uuid);
            if (game == null)
                throw new RuntimeException("parsed a game from a UUID but referenced to null");
            return game;
        }
        return null;
    }

}
