package me.notsodelayed.simmygameapi.api.map;

import java.io.File;
import java.util.Optional;

import me.notsodelayed.simmygameapi.api.game.Game;
import org.bukkit.plugin.java.JavaPlugin;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

/**
 * Represents a map for a {@link Game}.
 * Developers shall extend from this class to make use of the current implementation for their game map.
 */
public class GameMap {

    private final String id;
    private String displayName;
    private final File mapDirectory;
    private final GameMapConfig config;

    /**
     * @param plugin the plugin, for its data folder where the map originates
     * @param id the map id, also used for locating its origin world directory
     * @param displayName the map display name
     */
    public GameMap(@NotNull JavaPlugin plugin, @NotNull String id, @Nullable String displayName) {
        mapDirectory = new File(plugin.getDataFolder(), "maps" + File.separator + id);
        config = new GameMapConfig(mapDirectory, this);
        this.id = id;
        this.displayName = displayName;
    }

    /**
     * @return the file where this map is located
     */
    public File getDirectory() {
        return mapDirectory;
    }

    /**
     * @return the id
     */
    public String getId() {
        return id;
    }

    /**
     * @return the display name
     */
    @Nullable
    public String getDisplayName() {
        return displayName;
    }

    /**
     * @return an optional of the display name
     */
    @NotNull
    public Optional<String> getOptionalDisplayName() {
        return Optional.ofNullable(displayName);
    }

    /**
     * @param displayName the display name
     */
    public void setDisplayName(@Nullable String displayName) {
        this.displayName = displayName;
    }

}
