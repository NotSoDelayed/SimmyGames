package me.notsodelayed.towerdefense.game;

import java.io.File;

import org.jetbrains.annotations.NotNull;

import me.notsodelayed.simmygameapi.api.map.FixedMap;

public class DefenseMap extends FixedMap {

    public DefenseMap(@NotNull String id, @NotNull File mapDirectory) {
        super(id, mapDirectory);
    }

}
