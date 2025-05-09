package me.notsodelayed.simmygameapi.api;

import org.apache.commons.lang3.StringUtils;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import me.notsodelayed.simmygameapi.api.util.Identifiable;

/**
 * Represents a map of a {@link Game}.
 */
public class GameMap implements Identifiable {

    private final @NotNull String id;
    private final @Nullable String displayName;

    public GameMap(@NotNull String id, @Nullable String displayName) {
        this.id = id;
        this.displayName = displayName;
    }

    public GameMap(@NotNull String id) {
        this(id, StringUtils.capitalize(id.replaceAll("_", " ")));
    }

    @Override
    public @NotNull String id() {
        return id;
    }

    @Override
    public @Nullable String displayName() {
        return displayName;
    }

}
