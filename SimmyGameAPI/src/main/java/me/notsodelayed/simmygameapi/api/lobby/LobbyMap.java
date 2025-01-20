package me.notsodelayed.simmygameapi.api.lobby;

import java.io.File;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import me.notsodelayed.simmygameapi.api.Map;

/**
 * Represents a waiting lobby of a game.
 */
public class LobbyMap extends Map {

    public LobbyMap(@NotNull String id, @Nullable String displayName, @NotNull File mapDirectory) {
        super(id, displayName, mapDirectory);
    }

    public LobbyMap(String id, File mapDirectory) {
        super(id, mapDirectory);
    }

}
