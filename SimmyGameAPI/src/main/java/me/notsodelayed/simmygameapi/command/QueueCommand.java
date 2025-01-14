package me.notsodelayed.simmygameapi.command;

import java.util.List;

import org.apache.commons.lang3.StringUtils;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import me.notsodelayed.simmygameapi.api.Matchmaking;
import me.notsodelayed.simmygameapi.api.game.Game;
import me.notsodelayed.simmygameapi.util.MessageUtil;

public class QueueCommand extends BaseCommand {

    public QueueCommand(String label) {
        super(label);
    }

    @Override
    public boolean onCommand(@NotNull CommandSender sender, @NotNull Command command, @NotNull String label, @NotNull String[] strings) {
        if (strings.length > 0) {
            if (strings[0].equals("list")) {
                sender.sendMessage(StringUtils.join(fetchQueueTypeCache(), ","));
                return true;
            }
            // game types arg
            if (!(sender instanceof Player player)) {
                MessageUtil.playerOnly(sender);
                return true;
            }
            Class<? extends Game> type = Matchmaking.valueOf(strings[0]);
            if (type == null) {
                sender.sendMessage("Game type '" + strings[0] + " doesn't exists.");
                return true;
            }
            if (Matchmaking.queue(player, type)) {
                sender.sendMessage("You have been placed in qeueue for " + type.getSimpleName() + "!");
            } else {
                sender.sendMessage("You are already in queue!");
            }
            return true;
        }
        sender.sendMessage("-- queue command");
        return true;
    }

    public static List<String> REGISTERED_GAMES_CACHE;

    private static List<String> fetchQueueTypeCache() {
        if (REGISTERED_GAMES_CACHE == null)
            REGISTERED_GAMES_CACHE = Matchmaking.getRegisteredGames();
        return REGISTERED_GAMES_CACHE;
    }

    @Override
    public @Nullable List<String> onTabComplete(@NotNull CommandSender sender, @NotNull Command command, @NotNull String label, @NotNull String[] strings) {
        if (!(sender instanceof Player))
            return null;
        if (strings.length <= 1) {
            return fetchQueueTypeCache();
        }
        return null;
    }

}
