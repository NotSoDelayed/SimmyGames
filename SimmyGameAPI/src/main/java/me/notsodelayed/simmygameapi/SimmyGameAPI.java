package me.notsodelayed.simmygameapi;

import java.util.logging.Logger;

import org.bukkit.Bukkit;
import org.bukkit.plugin.java.JavaPlugin;

import me.notsodelayed.simmygameapi.command.GameCommand;
import me.notsodelayed.simmygameapi.command.QueueCommand;
import me.notsodelayed.simmygameapi.command.StatsCommand;
import me.notsodelayed.simmygameapi.handler.internal.PlayerProtectionHandler;

public final class SimmyGameAPI extends JavaPlugin {

    public static SimmyGameAPI instance;
    public static Logger logger;

    @Override
    public void onEnable() {
        instance = this;
        logger = getLogger();
        Config.get(this);

        initEventListeners();
        initCommands();

        logger.info("Successfully loaded Game API!");
    }

    private void initEventListeners() {
        // Game lobby protection
        Bukkit.getPluginManager().registerEvents(new PlayerProtectionHandler(), this);
    }

    private void initCommands() {
        new GameCommand("game");
        new StatsCommand("stats");
        new QueueCommand("queue");
    }

}
