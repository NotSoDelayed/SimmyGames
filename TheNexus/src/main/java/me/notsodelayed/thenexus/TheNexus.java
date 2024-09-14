package me.notsodelayed.thenexus;

import java.util.logging.Logger;

import org.bukkit.Bukkit;
import org.bukkit.plugin.java.JavaPlugin;

import me.notsodelayed.thenexus.config.Config;
import me.notsodelayed.thenexus.handler.internal.ServerPlayerJoinQuitHandler;

public final class TheNexus extends JavaPlugin {

    public static TheNexus instance;
    public static Logger logger;

    @Override
    public void onEnable() {
        long start = System.currentTimeMillis();
        instance = this;
        logger = getLogger();

        logger.info("");
        logger.info("||||||||  ||    ||  ||||||  ||    ||  ||||||  ||  ||  ||   ||   |||||");
        logger.info("   ||     ||    ||  ||      ||||  ||  ||      ||  ||  ||   ||  ||    ");
        logger.info("   ||     ||||||||  ||||    ||  ||||  ||||      ||    ||   ||   |||| ");
        logger.info("   ||     ||    ||  ||      ||    ||  ||      ||  ||  ||   ||      ||");
        logger.info("   ||     ||    ||  ||||||  ||    ||  ||||||  ||  ||   |||||   ||||| ");
        logger.info("");
        logger.info("Initialising...");

        Config.get();

        Bukkit.getPluginManager().registerEvents(new ServerPlayerJoinQuitHandler(), this);

        logger.info("Welcome onboard! Nexus can now be damaged! (took " + (System.currentTimeMillis() - start) + "ms)");

        // TODO reenable game instance monitor
//        Bukkit.getScheduler().runTaskTimer(this, () -> {
//            if (gameManager.getGames().size() < Config.get().MAX_ACTIVE_GAMES) {
//                NexusGame<NexusTeam, NexusPlayer> nexusGame = gameManager.createGame(1, 24, NexusMapManager.get().generateMapChoice(2));
//                nexusGame.ready();
//                TheNexus.logger.info("Nexus game deployed: " + nexusGame.getUuid());
//            }
//        }, 1, 200);
    }

    @Override
    public void onDisable() {
        logger.info("Nexus has been destroyed! Good bye...");
    }

}
