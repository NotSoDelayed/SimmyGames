package me.notsodelayed.thenexus.config;

import java.io.File;

import org.bukkit.command.CommandSender;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;

import me.notsodelayed.thenexus.TheNexus;

/**
 * Represents the config of {@link TheNexus}.
 */
public class Config {

    private static Config config;
    private FileConfiguration yml;

    // Config values
    public short MAX_ACTIVE_GAMES = 1;
    public boolean SOFT_EXCEPTIONS_FOR_ENDING_GAMES = false;
    public short CONFIG_VERSION = 1;

    private Config(TheNexus ignored) {}

    private static boolean init(TheNexus plugin) {
        Config currentConfig = config != null ? config : null;
        try {
            config = new Config(plugin);
            File configFile = new File(plugin.getDataFolder(), "config.yml");
            plugin.saveDefaultConfig();
            config.yml = YamlConfiguration.loadConfiguration(configFile);
            config.MAX_ACTIVE_GAMES = ((Number) config.yml.getInt("max-active-games", config.MAX_ACTIVE_GAMES)).shortValue();
            config.SOFT_EXCEPTIONS_FOR_ENDING_GAMES = config.yml.getBoolean("soft-exceptions-for-ending-games", config.SOFT_EXCEPTIONS_FOR_ENDING_GAMES);
            return true;
        } catch (Exception ex) {
            config = currentConfig;
            ex.printStackTrace(System.err);
            return false;
        }
    }

    public static Config get() {
        if (config == null && !init(TheNexus.instance))
            TheNexus.logger.warning("An error occurred whilst loading the config! The default-embedded values will be used instead.");
        return config;
    }

    /**
     * @param requester the requester
     * @return whether this request is approved and proceeds
     */
    public static boolean reload(CommandSender requester) {
        if (!init(TheNexus.instance)) {
            TheNexus.logger.warning("An error occurred whilst reloading the config! The current values will remain in use.");
            return false;
        }
        return true;
    }

}
