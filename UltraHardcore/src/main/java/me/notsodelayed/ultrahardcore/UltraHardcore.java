package me.notsodelayed.ultrahardcore;

import net.kyori.adventure.text.logger.slf4j.ComponentLogger;
import org.bukkit.plugin.java.JavaPlugin;

import me.notsodelayed.simmygameapi.util.Scheduler;
import me.notsodelayed.ultrahardcore.game.MiniUHCGame;

public class UltraHardcore extends JavaPlugin {

    private static UltraHardcore instance;
    private static ComponentLogger logger;
    private static Scheduler scheduler;

    @Override
    public void onEnable() {
        long start = System.currentTimeMillis();
        instance = this;
        logger = getComponentLogger();
        logger.info("");
        logger.info("||    ||  ||    ||    ||||||");
        logger.info("||    ||  ||    ||  ||      ");
        logger.info("||    ||  ||||||||  ||      ");
        logger.info("||    ||  ||    ||  ||      ");
        logger.info("  ||||    ||    ||    ||||||");
        logger.info("");
        logger.info("Initialising...");
        scheduler = new Scheduler(this);
        MiniUHCGame.register();
        logger.info("Welcome onboard! Ready to survive! (took " + (System.currentTimeMillis() - start) + "ms)");
    }

    public static UltraHardcore instance() {
        return instance;
    }

    public static ComponentLogger logger() {
        return logger;
    }

    public static Scheduler scheduler() {
        return scheduler;
    }

}
