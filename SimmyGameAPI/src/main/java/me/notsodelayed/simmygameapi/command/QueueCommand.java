package me.notsodelayed.simmygameapi.command;

import java.util.List;

import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import me.notsodelayed.simmygameapi.api.Matchmaking;

public class QueueCommand extends BaseCommand {

    public QueueCommand(String label) {
        super(label);
    }

    @Override
    public boolean onCommand(@NotNull CommandSender commandSender, @NotNull Command command, @NotNull String label, @NotNull String[] strings) {
        return true;
    }

    public static List<String> REGISTERED_GAMES_CACHE;

    @Override
    public @Nullable List<String> onTabComplete(@NotNull CommandSender sender, @NotNull Command command, @NotNull String label, @NotNull String[] strings) {
        if (!(sender instanceof Player))
            return null;
        if (strings.length <= 1) {
            if (REGISTERED_GAMES_CACHE == null)
                REGISTERED_GAMES_CACHE = Matchmaking.getRegisteredGames();
            return REGISTERED_GAMES_CACHE;
        }
        return null;
    }

}
