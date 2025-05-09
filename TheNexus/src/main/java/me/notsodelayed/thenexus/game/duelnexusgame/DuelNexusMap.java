package me.notsodelayed.thenexus.game.duelnexusgame;

import java.io.File;

import org.jetbrains.annotations.NotNull;

import me.notsodelayed.simmygameapi.util.Position;
import me.notsodelayed.thenexus.NexusMap;

public class DuelNexusMap extends NexusMap {

    private final Position nexusAlpha, nexusBeta;
    private final Position vaultIronAlpha, vaultIronBeta;

    @SuppressWarnings("DataFlowIssue")
    public DuelNexusMap(@NotNull String id, @NotNull File mapDirectory) {
        super(id, mapDirectory);
        nexusAlpha = Position.fromString(yaml().getString("map.nexus.red"));
        nexusBeta = Position.fromString(yaml().getString("map.nexus.blue"));
        vaultIronAlpha = Position.fromString(yaml().getString("map.vault.red"));
        vaultIronBeta = Position.fromString(yaml().getString("map.vault.blue"));
    }

    public Position getNexusAlpha() {
        return nexusAlpha.clone();
    }

    public Position getNexusBeta() {
        return nexusBeta.clone();
    }

    public Position getVaultIronAlpha() {
        return vaultIronAlpha.clone();
    }

    public Position getVaultIronBeta() {
        return vaultIronBeta.clone();
    }
}
