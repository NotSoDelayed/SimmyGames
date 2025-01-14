package me.notsodelayed.simmygameapi.api.game.map;

import java.io.File;
import java.util.Optional;

import org.apache.commons.lang3.StringUtils;
import org.bukkit.configuration.file.YamlConfiguration;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.yaml.snakeyaml.error.YAMLException;

import me.notsodelayed.simmygameapi.SimmyGameAPI;
import me.notsodelayed.simmygameapi.api.game.Game;
import me.notsodelayed.simmygameapi.util.FileUtil;

/**
 * Represents a map for a {@link Game}.
 * Developers shall extend from this class to make use of the current implementation for their game map.
 */
public class GameMap implements Cloneable {

    private final String id;
    private final String displayName;
    private final File mapDirectory;
    private YamlConfiguration yaml;

    /**
     * @throws YAMLException if the directory is not a valid map directory
     */
    public GameMap(@NotNull String id, @Nullable String displayName, @NotNull File mapDirectory) {
        FileUtil.checkIsDirectoryOrThrow(mapDirectory);
        this.id = id;
        this.displayName = displayName;
        this.mapDirectory = mapDirectory;
        loadYaml();
    }

    public GameMap(@NotNull String id, @NotNull File mapDirectory) {
        this(id, StringUtils.capitalize(id), mapDirectory);
    }

    protected void loadYaml() {
        File ymlFile = new File(mapDirectory, "map.yml");
        yaml = new YamlConfiguration();
        if (ymlFile.exists()) {
            try {
                yaml.load(ymlFile);
            } catch (Exception ex) {
                SimmyGameAPI.logger.warning("Exception occurred whilst reading map data of " + mapDirectory.getPath() + " -- Proceeding without any custom data...");
                ex.printStackTrace(System.err);
            }
        } else {
            throw new YAMLException("Missing map.yml file");
        }
    }

    public Optional<String> getDisplayName() {
        return Optional.ofNullable(displayName);
    }

    public String getId() {
        return id;
    }

    public File getDirectory() {
        return mapDirectory;
    }

    protected YamlConfiguration getYaml() {
        return yaml;
    }

    @Override
    public GameMap clone() {
        try {
            GameMap clone = (GameMap) super.clone();
            clone.loadYaml();
            return clone;
        } catch (CloneNotSupportedException e) {
            throw new AssertionError();
        }
    }
}
