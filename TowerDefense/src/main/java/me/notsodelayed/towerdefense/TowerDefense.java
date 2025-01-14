package me.notsodelayed.towerdefense;

import java.util.logging.Logger;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.plugin.java.JavaPlugin;

import me.notsodelayed.simmygameapi.lib.fastinv.FastInv;
import me.notsodelayed.simmygameapi.lib.fastinv.FastInvManager;
import me.notsodelayed.towerdefense.game.DefenseGame;

public class TowerDefense extends JavaPlugin {

    public static TowerDefense instance;
    public static Logger logger;

    @Override
    public void onEnable() {
        long start = System.currentTimeMillis();
        instance = this;
        logger = getLogger();
        logger.info("");
        logger.info("||||||    ||||   ||      ||  ||||||  ||||||");
        logger.info("  ||    ||    || ||      ||  ||      ||    ||");
        logger.info("  ||    ||    || ||  ||  ||  ||||    ||||||  ");
        logger.info("  ||    ||    || ||  ||  ||  ||      ||    ||");
        logger.info("  ||      ||||     ||  ||    ||||||  ||    ||");
        logger.info("  D      E      F      E      N      S      E");
        logger.info("");
        logger.info("Initialising...");

        FastInvManager.register(this);
        DefenseGame.register();

        Bukkit.getPluginManager().registerEvents(new Listener() {
            @EventHandler
            public void useItem(PlayerInteractEvent event) {
                ItemStack itemStack = event.getItem();
                if (itemStack != null && ChatColor.stripColor(itemStack.getItemMeta().getDisplayName()).equalsIgnoreCase("buildinggui")) {
                }
            }
        }, this);

        logger.info("Welcome onboard! (took " + (System.currentTimeMillis() - start) + "ms)");

    }

    @Override
    public void onDisable() {
        logger.info("Towers have been destroyed! Good bye...");
    }

}
