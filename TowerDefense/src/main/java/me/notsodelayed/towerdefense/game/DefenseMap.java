package me.notsodelayed.towerdefense.game;

import java.io.File;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import me.notsodelayed.simmygameapi.api.game.map.GameMap;

public class DefenseMap extends GameMap {

    public DefenseMap(@NotNull String id, @Nullable String displayName, @NotNull File mapDirectory) {
        super(id, displayName, mapDirectory);
    }

}
