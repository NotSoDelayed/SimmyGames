package me.notsodelayed.simmygameapi;

import java.util.logging.Logger;

import me.notsodelayed.simmygameapi.handler.internal.ServerPlayerProtectionHandler;
import org.bukkit.Bukkit;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.scoreboard.Scoreboard;

public final class SimmyGameAPI extends JavaPlugin {

    public static SimmyGameAPI instance;
    public static Logger logger;
    public Scoreboard scoreboard;

    @Override
    public void onEnable() {
        instance = this;
        logger = getLogger();
        scoreboard = Bukkit.getScoreboardManager().getNewScoreboard();
        Config.get(this);

        // Register game event handlers
        Bukkit.getPluginManager().registerEvents(new ServerPlayerProtectionHandler(), this);

        logger.info("Successfully loaded Game API!");
    }

    /**
     * @return the scoreboard tracked by this plugin
     */
    public Scoreboard getScoreboard() {
        return scoreboard;
    }

}
