package me.notsodelayed.simmygameapi.command;

import org.bukkit.Bukkit;
import org.bukkit.command.TabExecutor;

import me.notsodelayed.simmygameapi.util.LoggerUtil;

// TODO get rid of this
public abstract class BaseCommandOld implements TabExecutor {

    protected BaseCommandOld(String label) {
        Bukkit.getPluginCommand(label).setExecutor(this);
        LoggerUtil.verbose("[Command] Registered command: " + label);
    }

}
