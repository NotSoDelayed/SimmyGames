package me.notsodelayed.thenexus.map;

import me.notsodelayed.simmygameapi.api.map.GameMap;
import me.notsodelayed.thenexus.TheNexus;
import org.jetbrains.annotations.NotNull;

public class NexusGameMap extends GameMap {

    public NexusGameMap(@NotNull String id) {
        super(TheNexus.instance, id, null);
    }

}
