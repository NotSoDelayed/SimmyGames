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
    private final String displayName;
    private final File mapDirectory;

    public GameMap(@NotNull String id, @Nullable String displayName, @NotNull File mapDirectory) {
        FileUtil.checkIsDirectoryOrThrow(mapDirectory);
        this.id = id;
        this.displayName = displayName;
        this.mapDirectory = mapDirectory;
    }

    public GameMap(@NotNull String id, @NotNull File mapDirectory) {
        this(id, id, mapDirectory);
    }

    public Optional<String> getDisplayName() {
        return Optional.ofNullable(displayName);
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

}
