package me.notsodelayed.thenexus.map;

import java.io.File;

import me.notsodelayed.simmygameapi.api.game.map.GameMap;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public class NexusMap extends GameMap {

    public NexusMap(@NotNull String id, @Nullable String displayName, @NotNull File mapDirectory) {
        super(id, displayName, mapDirectory);
    }

}
