package me.notsodelayed.thenexus.map;

import java.io.File;

import org.jetbrains.annotations.NotNull;

import me.notsodelayed.simmygameapi.util.Position;

public class DuelNexusMap extends NexusMap {

    private final Position nexusAlpha, nexusBeta;

    @SuppressWarnings("DataFlowIssue")
    public DuelNexusMap(@NotNull String id, @NotNull File mapDirectory) {
        super(id, mapDirectory);
        nexusAlpha = Position.fromString(getYaml().getString("map.nexus.red"));
        nexusBeta = Position.fromString(getYaml().getString("map.nexus.blue"));
    }

    public Position getNexusAlpha() {
        return nexusAlpha.clone();
    }

    public Position getNexusBeta() {
        return nexusBeta.clone();
    }

}
