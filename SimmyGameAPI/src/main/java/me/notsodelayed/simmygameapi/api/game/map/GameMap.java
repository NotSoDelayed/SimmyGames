package me.notsodelayed.simmygameapi.api.game.map;

import java.io.File;
import java.util.Optional;

import org.bukkit.configuration.file.YamlConfiguration;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import me.notsodelayed.simmygameapi.api.game.Game;
import me.notsodelayed.simmygameapi.util.FileUtil;

/**
 * Represents a map for a {@link Game}.
 * Developers shall extend from this class to make use of the current implementation for their game map.
 */
public class GameMap {

    private final String id;
    private Optional<String> displayName;
    private final File mapDirectory;
    private final File yamlFile;
    private YamlConfiguration yaml;

    public GameMap(@NotNull String id, @Nullable String displayName, @NotNull File mapDirectory) {
        this.id = id;
        this.displayName = Optional.ofNullable(displayName);
        FileUtil.checkIsDirectoryOrThrow(mapDirectory);
        this.mapDirectory = mapDirectory;
        yamlFile = new File(mapDirectory, "map.yml");
    }

    public GameMap(@NotNull String id, @NotNull File mapDirectory) {
        this(id, null, mapDirectory);
        displayName = Optional.ofNullable(yaml.getString("display-name", null));
    }

    public Optional<String> getDisplayName() {
        return displayName;
    }

    /**
     * @return the id
     */
    public String getId() {
        return id;
    }

    /**
     * @return the directory of this map
     */
    public File getDirectory() {
        return mapDirectory;
    }

    /**
     * @return the YAML file of this map (map.yml)
     */
    public File getYamlFile() {
        return yamlFile;
    }

    /**
     * @return the YAML instance associated
     */
    public YamlConfiguration getYaml() {
        return yaml;
    }

}
