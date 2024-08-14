package me.notsodelayed.simmygameapi.command;

import org.bukkit.Bukkit;
import org.bukkit.command.TabExecutor;

import me.notsodelayed.simmygameapi.util.LoggerUtil;

public abstract class BaseCommand implements TabExecutor {

    protected BaseCommand(String label) {
        Bukkit.getPluginCommand(label).setExecutor(this);
        LoggerUtil.verbose("Registered command: " + label);
    }

}
