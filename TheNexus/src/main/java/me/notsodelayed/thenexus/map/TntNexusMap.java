package me.notsodelayed.thenexus.map;

import java.io.File;

import org.jetbrains.annotations.NotNull;

import me.notsodelayed.simmygameapi.util.Position;
import me.notsodelayed.thenexus.NexusMap;

public class TntNexusMap extends NexusMap {

    private final Position nexusAlphaLeft, nexusAlphaMiddle, nexusAlphaRight;
    private final Position nexusBetaLeft, nexusBetaMiddle, nexusBetaRight;

    @SuppressWarnings("DataFlowIssue")
    public TntNexusMap(@NotNull String id, @NotNull File mapDirectory) throws RuntimeException {
        super(id, mapDirectory);
        nexusAlphaLeft = Position.fromString(yaml().getString("map.nexus.red.left"));
        nexusAlphaMiddle = Position.fromString(yaml().getString("map.nexus.red.middle"));
        nexusAlphaRight = Position.fromString(yaml().getString("map.nexus.red.right"));
        nexusBetaLeft = Position.fromString(yaml().getString("map.nexus.blue.left"));
        nexusBetaMiddle = Position.fromString(yaml().getString("map.nexus.blue.middle"));
        nexusBetaRight = Position.fromString(yaml().getString("map.nexus.blue.right"));
    }

    public Position getNexusAlphaLeft() {
        return nexusAlphaLeft.clone();
    }

    public Position getNexusAlphaMiddle() {
        return nexusAlphaMiddle.clone();
    }

    public Position getNexusAlphaRight() {
        return nexusAlphaRight.clone();
    }

    public Position getNexusBetaLeft() {
        return nexusBetaLeft.clone();
    }

    public Position getNexusBetaMiddle() {
        return nexusBetaMiddle.clone();
    }

    public Position getNexusBetaRight() {
        return nexusBetaRight.clone();
    }

//    @Override
//    public TntNexusMap clone() {
//        TntNexusMap clone = (TntNexusMap) super.clone();
//        clone.nexusAlphaLeft = getNexusAlphaLeft();
//        clone.nexusAlphaMiddle = getNexusAlphaMiddle();
//        clone.nexusAlphaRight = getNexusAlphaRight();
//        clone.nexusBetaLeft = getNexusBetaLeft();
//        clone.nexusBetaMiddle = getNexusBetaMiddle();
//        clone.nexusBetaRight = getNexusBetaRight();
//        return clone;
//    }

}
