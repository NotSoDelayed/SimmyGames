package me.notsodelayed.simmygameapi;

import java.util.logging.Logger;

import dev.jorel.commandapi.CommandAPI;
import dev.jorel.commandapi.CommandAPIBukkitConfig;
import net.kyori.adventure.text.minimessage.MiniMessage;
import org.bukkit.Bukkit;
import org.bukkit.World;
import org.bukkit.plugin.java.JavaPlugin;

import me.notsodelayed.simmygameapi.api.game.Game;
import me.notsodelayed.simmygameapi.api.game.MapGame;
import me.notsodelayed.simmygameapi.commands.GameCommand;
import me.notsodelayed.simmygameapi.handler.PlayerProtectionHandler;
import me.notsodelayed.simmygameapi.util.Scheduler;
import me.notsodelayed.simmygameapi.util.Util;

public final class SimmyGameAPI extends JavaPlugin {

    public static final String ADMIN_PERMISSION = "simmygameapi.game.admin";
    public static SimmyGameAPI instance;
    public static Logger logger;
    private static final MiniMessage MINI_MESSAGE = MiniMessage.miniMessage();
    private static Scheduler SCHEDULER;

    private static GameCommand gameCommand;

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
        gameCommand.tabCompleteListener();
        initEventListeners();
        logger.info("Successfully loaded Game API!");
    }

    @Override
    public void onDisable() {
        CommandAPI.onDisable();
        // Force unload world of MapGames ourselves otherwise server will save them
        Game.getGames().values().forEach(game -> {
            if (game instanceof MapGame<?> mapGame) {
                World gameWorld = Bukkit.getWorld(mapGame.getGameWorldName());
                if (gameWorld != null) {
                    gameWorld.getPlayers().forEach(player -> {
                        player.teleportAsync(Util.getMainWorld().getSpawnLocation());
                    });
                    Bukkit.unloadWorld(gameWorld, false);
                }
            }
        });
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
        CommandAPI.onLoad(new CommandAPIBukkitConfig(this).skipReloadDatapacks(true).silentLogs(true));
        gameCommand = new GameCommand("game");
    }


}
