package me.notsodelayed.simmygameapi;

import java.io.File;

import org.bukkit.command.CommandSender;
import org.bukkit.configuration.file.YamlConfiguration;

public class Config {

    public static Config config;

    public boolean VERBOSE = false;

    private Config(SimmyGameAPI ignored) {}

    private static boolean init(SimmyGameAPI plugin) {
        Config currentConfig = config != null ? config : null;
        try {
            config = new Config(plugin);
            plugin.saveDefaultConfig();
            YamlConfiguration yml = YamlConfiguration.loadConfiguration(new File(plugin.getDataFolder(), "config.yml"));
            config.VERBOSE = yml.getBoolean("verbose", config.VERBOSE);
            return true;
        } catch (Exception ex) {
            config = currentConfig;
            ex.printStackTrace(System.err);
            return false;
        }
    }

    public static Config get(SimmyGameAPI plugin) {
        if (config == null && !init(plugin))
            SimmyGameAPI.logger.warning("An error occurred whilst loading the config! The default-embedded values will be used instead.");
        return config;
    }

    public static boolean reload(CommandSender requester) {
        if (!init(SimmyGameAPI.instance)) {
            SimmyGameAPI.logger.warning("An error occurred whilst reloading the config! The current values will remain in use.");
            return false;
        }
        return true;
    }


}
