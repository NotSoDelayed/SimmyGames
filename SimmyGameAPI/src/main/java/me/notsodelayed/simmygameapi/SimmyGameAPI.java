package me.notsodelayed.simmygameapi;

import java.util.logging.Logger;

import org.bukkit.Bukkit;
import org.bukkit.plugin.java.JavaPlugin;

import me.notsodelayed.simmygameapi.command.GameCommand;
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

        initCommands();
        initEventListeners();

        logger.info("Successfully loaded Game API!");
    }

    private void initEventListeners() {
        // Game lobby protection
        Bukkit.getPluginManager().registerEvents(new PlayerProtectionHandler(), this);
//        Bukkit.getPluginManager().registerEvents(new Listener() {
//            @EventHandler
//            public void onJoin(PlayerJoinEvent event) {
//                logger.info("Testing actionbar...");
//                Bukkit.getScheduler().runTaskTimer(instance, new BukkitRunnable() {
//                    @Override
//                    public void run() {
//                        if (!event.getPlayer().isOnline()) {
//                            logger.info("Player left.. Ending test");
//                            cancel();
//                            return;
//                        }
//                        NMSUtil.sendActionBar(event.getPlayer(), StringUtil.color("&bFancy: " + UUID.randomUUID()));
//                    }
//                }, 10, 20);
//            }
//        }, this);
    }

    private void initCommands() {
        new GameCommand("game");
        new StatsCommand("stats");
    }

}
