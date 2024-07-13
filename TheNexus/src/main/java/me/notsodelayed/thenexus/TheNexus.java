package me.notsodelayed.thenexus;

import java.io.File;
import java.util.logging.Logger;

import me.notsodelayed.thenexus.command.KitPromptCommand;
import me.notsodelayed.thenexus.command.TheNexusCommand;
import me.notsodelayed.thenexus.config.Config;
import me.notsodelayed.thenexus.game.NexusGame;
import me.notsodelayed.thenexus.game.NexusGameManager;
import me.notsodelayed.thenexus.handler.internal.ServerPlayerJoinQuitHandler;
import me.notsodelayed.thenexus.kit.NexusKit;
import me.notsodelayed.thenexus.kit.NexusKitManager;
import me.notsodelayed.thenexus.map.NexusMapManager;
import org.bukkit.Bukkit;
import org.bukkit.plugin.java.JavaPlugin;
import org.jetbrains.annotations.ApiStatus;

public final class TheNexus extends JavaPlugin {

    public static TheNexus instance;
    public static File pluginFile;
    public static Logger logger;

    @Override
    public void onEnable() {
        long start = System.currentTimeMillis();
        instance = this;
        pluginFile = this.getFile();
        logger = getLogger();
        logger.info("");
        logger.info("||||||||  ||    ||  ||||||  ||    ||  ||||||  ||  ||  ||   ||   |||||");
        logger.info("   ||     ||    ||  ||      ||||  ||  ||      ||  ||  ||   ||  ||    ");
        logger.info("   ||     ||||||||  ||||    ||  ||||  ||||      ||    ||   ||   |||| ");
        logger.info("   ||     ||    ||  ||      ||    ||  ||      ||  ||  ||   ||      ||");
        logger.info("   ||     ||    ||  ||||||  ||    ||  ||||||  ||  ||   |||||   ||||| ");
        logger.info("");
        logger.info("Initializing...");
        Config.get();
        NexusMapManager.get();
        NexusKitManager.get();
        NexusGameManager gameManager = NexusGameManager.get();

        TheNexusCommand.register();
        KitPromptCommand.register();

        Bukkit.getPluginManager().registerEvents(new ServerPlayerJoinQuitHandler(), this);

        logger.info("Welcome onboard! Nexus can now be damaged! (took " + (System.currentTimeMillis() - start) + "ms)");

        // Game instance monitor
        Bukkit.getScheduler().runTaskTimer(this, () -> {
            if (gameManager.getGames().size() < Config.get().MAX_ACTIVE_GAMES) {
                NexusGame nexusGame = gameManager.createGame(1, 24, NexusMapManager.get().generateMapChoice(2));
                nexusGame.ready();
                TheNexus.logger.info("Nexus game deployed: " + nexusGame.getUuid());
            }
        }, 1, 200);
    }

    @Override
    public void onDisable() {
        logger.info("Nexus has been destroyed! Good bye...");
    }

}
