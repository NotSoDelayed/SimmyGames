package me.notsodelayed.thenexus.map;

import java.io.File;

import org.jetbrains.annotations.NotNull;

import me.notsodelayed.simmygameapi.api.game.map.GameMap;
import me.notsodelayed.simmygameapi.util.Position;

public class NexusMap extends GameMap {

    private final Position nexusAlpha, nexusBeta;
    private final Position spawnAlpha, spawnBeta;

    public NexusMap(@NotNull String id, @NotNull File mapDirectory) throws RuntimeException {
        super(id, mapDirectory);
        nexusAlpha = Position.fromString(getYaml().getString("map.nexus.red"));
        nexusBeta = Position.fromString(getYaml().getString("map.nexus.blue"));
        spawnAlpha = Position.fromString(getYaml().getString("map.spawn.red"));
        spawnBeta = Position.fromString(getYaml().getString("map.spawn.blue"));
    }

    public Position getNexusAlpha() {
        return nexusAlpha;
    }

    public Position getNexusBeta() {
        return nexusBeta;
    }

    public Position getSpawnAlpha() {
        return spawnAlpha;
    }

    public Position getSpawnBeta() {
        return spawnBeta;
    }

}
