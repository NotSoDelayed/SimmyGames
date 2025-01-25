package me.notsodelayed.thenexus;

import java.util.logging.Logger;

import org.bukkit.plugin.java.JavaPlugin;

import me.notsodelayed.thenexus.config.Config;
import me.notsodelayed.thenexus.game.DuelNexusGame;

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
        DuelNexusGame.register();

        logger.info("Welcome onboard! Nexus can now be damaged! (took " + (System.currentTimeMillis() - start) + "ms)");

    }

    @Override
    public void onDisable() {
        logger.info("Nexus has been destroyed! Good bye...");
    }

}
