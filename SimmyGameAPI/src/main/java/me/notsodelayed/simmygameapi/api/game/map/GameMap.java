package me.notsodelayed.simmygameapi.api.game.map;

import java.io.File;
import java.util.Optional;

import org.apache.commons.lang3.StringUtils;
import org.bukkit.configuration.file.YamlConfiguration;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import me.notsodelayed.simmygameapi.SimmyGameAPI;
import me.notsodelayed.simmygameapi.api.game.Game;
import me.notsodelayed.simmygameapi.util.FileUtil;

/**
 * Represents a map for a {@link Game}.
 * Developers shall extend from this class to make use of the current implementation for their game map.
 */
public class GameMap {

    private final String id;
    private final String displayName;
    private final File mapDirectory;
    private final YamlConfiguration yaml;

    public GameMap(@NotNull String id, @Nullable String displayName, @NotNull File mapDirectory) {
        FileUtil.checkIsDirectoryOrThrow(mapDirectory);
        this.id = id;
        this.displayName = displayName;
        this.mapDirectory = mapDirectory;
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
            SimmyGameAPI.logger.warning(mapDirectory.getPath() + " does not have a 'map.yml' -- Proceeding without any custom data...");
        }
    }

    public GameMap(@NotNull String id, @NotNull File mapDirectory) {
        this(id, StringUtils.capitalize(id), mapDirectory);
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

}
