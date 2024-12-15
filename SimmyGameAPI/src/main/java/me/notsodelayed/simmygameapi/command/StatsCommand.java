package me.notsodelayed.simmygameapi.command;

import java.util.List;

import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import me.notsodelayed.simmygameapi.api.game.player.GamePlayer;
import me.notsodelayed.simmygameapi.api.game.player.StatisticsPlayer;
import me.notsodelayed.simmygameapi.util.MessageUtil;

public class StatsCommand extends BaseCommand {

    public StatsCommand(String label) {
        super(label);
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String s, String[] strings) {
        if (!(sender instanceof Player player)) {
            MessageUtil.playerOnly(sender);
            return true;
        }
        GamePlayer gamePlayer = GamePlayer.get(player);
        if (gamePlayer == null) {
            MessageUtil.sendErrorMessage(player, "You are currently not in a game to check your stats.");
            return true;
        }
        if (!(gamePlayer instanceof StatisticsPlayer statsPlayer)) {
            MessageUtil.sendErrorMessage(player, "The game you are currently in does not have player stats.");
            return true;
        }
        player.sendMessage("-- View stats");
        return true;
    }

    @Override
    public List<String> onTabComplete(CommandSender commandSender, Command command, String s, String[] strings) {
        return null;
    }

}
