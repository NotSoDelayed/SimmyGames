package me.notsodelayed.simmygameapi;

import java.util.logging.Logger;

import dev.jorel.commandapi.CommandAPI;
import dev.jorel.commandapi.CommandAPIBukkitConfig;
import net.kyori.adventure.text.minimessage.MiniMessage;
import org.bukkit.Bukkit;
import org.bukkit.plugin.java.JavaPlugin;

import me.notsodelayed.simmygameapi.api.feature.PlayerContainers;
import me.notsodelayed.simmygameapi.api.feature.RespawnableBlocks;
import me.notsodelayed.simmygameapi.commands.GameCommand;
import me.notsodelayed.simmygameapi.handler.PlayerProtectionHandler;
import me.notsodelayed.simmygameapi.util.Scheduler;

public final class SimmyGameAPI extends JavaPlugin {

    public static final String ADMIN_PERMISSION = "simmygameapi.game.admin";
    public static SimmyGameAPI instance;
    public static Logger logger;
    private static final MiniMessage MINI_MESSAGE = MiniMessage.miniMessage();
    private static Scheduler SCHEDULER;

    @Override
    public void onLoad() {
        initCommands();
    }

    @Override
    public void onEnable() {
        instance = this;
        logger = getLogger();
        Config.get(this);
        SCHEDULER = new Scheduler(this);
        if (Bukkit.getPluginManager().getPlugin("VoidGen") == null) {
            logger.warning("Plugin 'VoidGen' not found! It is required to load game worlds as void worlds!");
            logger.warning("Download at: https://github.com/xtkq-is-not-available/VoidGen/releases");
        }
        CommandAPI.onEnable();
        registerMapGameFeatures();

        // TODO refractor this once Lobby API is made
        initProtectionListeners();
        logger.info("Successfully loaded Game API!");
    }

    @Override
    public void onDisable() {
        CommandAPI.onDisable();
    }

    public static MiniMessage miniMessage() {
        return MINI_MESSAGE;
    }

    public static Scheduler scheduler() {
        return SCHEDULER;
    }

    private void initProtectionListeners() {
        // Game lobby protection
        Bukkit.getPluginManager().registerEvents(new PlayerProtectionHandler(), this);
    }

    private void initCommands() {
        CommandAPI.onLoad(new CommandAPIBukkitConfig(this).skipReloadDatapacks(true).silentLogs(true));
        new GameCommand("game");
    }

    private void registerMapGameFeatures() {
        RespawnableBlocks.init();
        PlayerContainers.init();
    }

}
