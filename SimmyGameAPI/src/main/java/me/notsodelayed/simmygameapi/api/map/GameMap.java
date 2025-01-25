package me.notsodelayed.simmygameapi.api.map;

import java.io.File;

import org.apache.commons.lang3.StringUtils;
import org.bukkit.configuration.file.YamlConfiguration;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.yaml.snakeyaml.error.YAMLException;

import me.notsodelayed.simmygameapi.SimmyGameAPI;
import me.notsodelayed.simmygameapi.api.Game;
import me.notsodelayed.simmygameapi.api.Map;

/**
 * Represents a map for a {@link Game}.
 * Developers shall extend from this class to make use of the current implementation for their game map.
 */
public class GameMap extends Map {

    private YamlConfiguration yaml;

    /**
     * @throws YAMLException if the directory is not a valid map directory
     */
    public GameMap(@NotNull String id, @Nullable String displayName, @NotNull File mapDirectory) {
        super(id, displayName, mapDirectory);
        loadYaml();
    }

    public GameMap(@NotNull String id, @NotNull File mapDirectory) {
        this(id, StringUtils.capitalize(id), mapDirectory);
    }

    protected void loadYaml() {
        File ymlFile = new File(getDirectory(), "map.yml");
        yaml = new YamlConfiguration();
        if (ymlFile.exists()) {
            try {
                yaml.load(ymlFile);
            } catch (Exception ex) {
                SimmyGameAPI.logger.warning("Exception occurred whilst reading map data of " + getDirectory().getPath() + " -- Proceeding without any custom data...");
                ex.printStackTrace(System.err);
            }
        } else {
            throw new YAMLException("Missing map.yml file");
        }
    }

    protected YamlConfiguration getYaml() {
        return yaml;
    }

}
