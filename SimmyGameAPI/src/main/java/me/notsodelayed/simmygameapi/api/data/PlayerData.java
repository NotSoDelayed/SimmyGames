package me.notsodelayed.simmygameapi.api.data;

import java.io.File;
import java.io.IOException;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;
import java.util.UUID;

import com.google.common.base.Preconditions;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.plugin.java.JavaPlugin;

import me.notsodelayed.simmygameapi.SimmyGameAPI;
import me.notsodelayed.simmygameapi.util.LoggerUtil;

/**
 * A data holder of a {@link Player}.
 */
public class PlayerData {

    private static final File DATA_DIRECTORY = new File(SimmyGameAPI.instance.getDataFolder(), "playerdata");
    private JavaPlugin plugin;
    private final UUID playerUuid;
    private final Map<Key<?>, Object> values;

    public PlayerData(JavaPlugin plugin, Player player) {
        this.plugin = plugin;
        this.playerUuid = player.getUniqueId();
        values = new HashMap<>();
    }

    public void put(Key<?> key, Object value) {
        Preconditions.checkArgument(key.getValueType().isAssignableFrom(value.getClass()), String.format("expected value type %s (got %s)", key.getValueType(), value.getClass()));
        values.put(key, value);
    }

    public Object get(Key<?> key) {
        return key.getValueType().cast(values.get(key));
    }

    public void save() throws IOException {
        File dataFile = new File(DATA_DIRECTORY, plugin.getName().toLowerCase(Locale.ENGLISH) + File.separator + playerUuid + ".yml");
        YamlConfiguration yml;
        if (dataFile.createNewFile()) {
            yml = new YamlConfiguration();
            LoggerUtil.verbose("Created new data file for " + playerUuid + "...");
        } else {
            yml = YamlConfiguration.loadConfiguration(dataFile);
        }
        values.forEach((key, value) -> yml.set(key.getKey(), value));
        yml.save(dataFile);
    }

    /**
     * @return the belonging plugin of this player data
     */
    public JavaPlugin getPlugin() {
        return plugin;
    }

    /**
     * @return {@link Player#getUniqueId()}
     */
    public UUID getUuid() {
        return playerUuid;
    }

}
