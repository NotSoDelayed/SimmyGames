package me.notsodelayed.simmygameapi.api.game.map;

import java.io.File;
import java.util.Optional;

import me.notsodelayed.simmygameapi.api.game.Game;
import me.notsodelayed.simmygameapi.util.FileUtil;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

/**
 * Represents a map for a {@link Game}.
 * Developers shall extend from this class to make use of the current implementation for their game map.
 */
public class GameMap {

    private final String id;
    @Nullable
    private final String displayName;
    private final File mapDirectory;

    public GameMap(@NotNull String id, @NotNull File mapDirectory) {
        this(id, null, mapDirectory);
    }

    public GameMap(@NotNull String id, @Nullable String displayName, @NotNull File mapDirectory) {
        this.id = id;
        this.displayName = displayName;
        FileUtil.checkIsDirectoryOrThrow(mapDirectory);
        this.mapDirectory = mapDirectory;
    }

    /**
     * @return the optional display name
     */
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
    public File getMapDirectory() {
        return mapDirectory;
    }

}
