package me.notsodelayed.thenexus;

import java.io.File;

import org.jetbrains.annotations.NotNull;

import me.notsodelayed.simmygameapi.api.map.FixedMap;
import me.notsodelayed.simmygameapi.util.Position;

public abstract class NexusMap extends FixedMap {

    private final Position spawnAlpha, spawnBeta;

    protected NexusMap(@NotNull String id, @NotNull File mapDirectory) throws RuntimeException {
        super(id, mapDirectory);
        spawnAlpha = Position.fromString(yaml().getString("map.spawn.red"));
        spawnBeta = Position.fromString(yaml().getString("map.spawn.blue"));
    }

    public Position getSpawnAlpha() {
        return spawnAlpha.clone();
    }

    public Position getSpawnBeta() {
        return spawnBeta.clone();
    }

}
