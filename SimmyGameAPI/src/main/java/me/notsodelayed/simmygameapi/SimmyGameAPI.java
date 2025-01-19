package me.notsodelayed.simmygameapi;

import java.util.logging.Logger;

import dev.jorel.commandapi.CommandAPI;
import dev.jorel.commandapi.CommandAPIBukkitConfig;
import net.kyori.adventure.text.minimessage.MiniMessage;
import org.bukkit.Bukkit;
import org.bukkit.plugin.java.JavaPlugin;

import me.notsodelayed.simmygameapi.command.v2.GameCommand;
import me.notsodelayed.simmygameapi.handler.internal.PlayerProtectionHandler;
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
        CommandAPI.onEnable();
        initEventListeners();
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

    private void initEventListeners() {
        // Game lobby protection
        Bukkit.getPluginManager().registerEvents(new PlayerProtectionHandler(), this);
    }

    private void initCommands() {
        CommandAPI.onLoad(new CommandAPIBukkitConfig(this));
        new GameCommand("game");
    }


}
