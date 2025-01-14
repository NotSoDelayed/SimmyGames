package me.notsodelayed.thenexus.map;

import java.io.File;

import org.jetbrains.annotations.NotNull;

import me.notsodelayed.simmygameapi.api.game.map.GameMap;
import me.notsodelayed.simmygameapi.util.Position;

public abstract class NexusMap extends GameMap {

    private final Position spawnAlpha, spawnBeta;

    protected NexusMap(@NotNull String id, @NotNull File mapDirectory) throws RuntimeException {
        super(id, mapDirectory);
        spawnAlpha = Position.fromString(getYaml().getString("map.spawn.red"));
        spawnBeta = Position.fromString(getYaml().getString("map.spawn.blue"));
    }

    public Position getSpawnAlpha() {
        return spawnAlpha.clone();
    }

    public Position getSpawnBeta() {
        return spawnBeta.clone();
    }

}
