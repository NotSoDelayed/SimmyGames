package me.notsodelayed.simmygameapi.api.map;

import net.kyori.adventure.util.TriState;
import org.bukkit.World;
import org.bukkit.WorldCreator;
import org.jetbrains.annotations.NotNull;

import me.notsodelayed.simmygameapi.api.GameMap;

/**
 * Represents a {@link GameMap} with a world that is naturally generated upon game start.
 */
public class NaturalMap extends GameMap {

    public NaturalMap(@NotNull String id) {
        super(id);
    }

    /**
     * World creator responsible for this world creation.
     * @return default: an overworld creator
     * @implNote Override this for custom {@link WorldCreator}
     */
    public @NotNull WorldCreator worldCreator() {
        return new WorldCreator(id())
                .environment(World.Environment.NORMAL)
                .keepSpawnLoaded(TriState.FALSE);
    }

}
