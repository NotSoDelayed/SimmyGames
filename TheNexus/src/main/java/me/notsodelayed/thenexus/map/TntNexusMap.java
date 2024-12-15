package me.notsodelayed.thenexus.map;

import java.io.File;

import org.jetbrains.annotations.NotNull;

import me.notsodelayed.simmygameapi.util.Position;

public class TntNexusMap extends NexusMap {

    private final Position nexusAlphaLeft, nexusAlphaMiddle, nexusAlphaRight;
    private final Position nexusBetaLeft, nexusBetaMiddle, nexusBetaRight;

    public TntNexusMap(@NotNull String id, @NotNull File mapDirectory) throws RuntimeException {
        super(id, mapDirectory);
        nexusAlphaLeft = Position.fromString(getYaml().getString("map.nexus.red.left"));
        nexusAlphaMiddle = Position.fromString(getYaml().getString("map.nexus.red.middle"));
        nexusAlphaRight = Position.fromString(getYaml().getString("map.nexus.red.right"));
        nexusBetaLeft = Position.fromString(getYaml().getString("map.nexus.blue.left"));
        nexusBetaMiddle = Position.fromString(getYaml().getString("map.nexus.blue.middle"));
        nexusBetaRight = Position.fromString(getYaml().getString("map.nexus.blue.right"));
    }

    public Position getNexusAlphaLeft() {
        return nexusAlphaLeft;
    }

    public Position getNexusAlphaMiddle() {
        return nexusAlphaMiddle;
    }

    public Position getNexusAlphaRight() {
        return nexusAlphaRight;
    }

    public Position getNexusBetaLeft() {
        return nexusBetaLeft;
    }

    public Position getNexusBetaMiddle() {
        return nexusBetaMiddle;
    }

    public Position getNexusBetaRight() {
        return nexusBetaRight;
    }

}
