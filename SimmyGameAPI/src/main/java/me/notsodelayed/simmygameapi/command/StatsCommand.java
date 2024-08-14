package me.notsodelayed.simmygameapi.command;

import java.util.List;

import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;

public class StatsCommand extends BaseCommand {

    public StatsCommand(String label) {
        super(label);
    }

    @Override
    public boolean onCommand(CommandSender commandSender, Command command, String s, String[] strings) {
        return false;
    }

    @Override
    public List<String> onTabComplete(CommandSender commandSender, Command command, String s, String[] strings) {
        return null;
    }

}
