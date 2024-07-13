package me.notsodelayed.simmygameapi.api.map;

import java.io.File;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

import me.notsodelayed.simmygameapi.api.game.Game;
import me.notsodelayed.simmygameapi.util.FileUtil;
import org.bukkit.configuration.file.YamlConfiguration;

/**
 * Represents a game map configuration for a {@link Game}.
 */
public class GameMapConfig {

    private final GameMap map;
    private final File configFile;
    private final YamlConfiguration data;
    private final Map<String, Object> valuesCache = new HashMap<>();

    /**
     * @param map the map to load the config
     * @throws NullPointerException if game.yml does not exist
     */
    public GameMapConfig(File mapsDirectory, GameMap map) throws NullPointerException {
        this.map = map;
        configFile = new File(mapsDirectory, "game.yml");
        FileUtil.checkExistsOrThrow(configFile);
        data = YamlConfiguration.loadConfiguration(configFile);

        loadOptionals();
    }

    /**
     * Loads optional values
     */
    private void loadOptionals() {
        Optional.ofNullable(data.getString("map.name")).ifPresent(map::setDisplayName);
    }

}
