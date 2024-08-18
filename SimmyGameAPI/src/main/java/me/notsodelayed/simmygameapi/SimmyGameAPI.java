package me.notsodelayed.simmygameapi;

import java.util.logging.Logger;

import org.bukkit.Bukkit;
import org.bukkit.plugin.java.JavaPlugin;

import me.notsodelayed.simmygameapi.command.GameCommand;
import me.notsodelayed.simmygameapi.command.StatsCommand;
import me.notsodelayed.simmygameapi.handler.internal.ServerPlayerProtectionHandler;
import me.notsodelayed.simmygameapi.util.DummyLocation;
import me.notsodelayed.simmygameapi.util.StringParser;

public final class SimmyGameAPI extends JavaPlugin {

    public static SimmyGameAPI instance;
    public static Logger logger;

    @Override
    public void onEnable() {
        instance = this;
        logger = getLogger();
        Config.get(this);

        initParsers();
        initCommands();
        initEventListeners();

        logger.info("Successfully loaded Game API!");
    }

    private void initParsers() {
        StringParser.addParser(DummyLocation.class, argument -> {
            String[] split = argument.split(",");
            if (split.length != 3 && split.length != 5)
                return null;
            double x, y, z, yaw = 0, pitch = 0;
            x = Double.parseDouble(split[0]);
            y = Double.parseDouble(split[1]);
            z = Double.parseDouble(split[2]);
            if (split.length == 5) {
                yaw = Double.parseDouble(split[3]);
                pitch = Double.parseDouble(split[4]);
            }
            return new DummyLocation(x, y, z, yaw, pitch);
        });
    }

    private void initEventListeners() {
        // Game lobby protection
        Bukkit.getPluginManager().registerEvents(new ServerPlayerProtectionHandler(), this);
    }

    private void initCommands() {
        new GameCommand("game");
        new StatsCommand("stats");
    }

}
