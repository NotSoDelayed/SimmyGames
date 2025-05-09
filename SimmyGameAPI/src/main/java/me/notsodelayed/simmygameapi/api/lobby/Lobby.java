package me.notsodelayed.simmygameapi.api.lobby;

import java.util.*;
import java.util.concurrent.CompletableFuture;

import net.kyori.adventure.util.TriState;
import org.bukkit.Bukkit;
import org.bukkit.World;
import org.bukkit.WorldCreator;
import org.bukkit.WorldType;

import me.notsodelayed.simmygameapi.SimmyGameAPI;
import me.notsodelayed.simmygameapi.api.Game;
import me.notsodelayed.simmygameapi.api.GameMap;
import me.notsodelayed.simmygameapi.api.GamePlayer;

/**
 * Represents a waiting lobby of a {@link Game}.
 */
public class Lobby {

    private static final Map<Class<? extends Game>, List<Lobby>> LOBBIES = new HashMap<>();
    private final Class<? extends Game> group;
    private final GameMap map;
    private final UUID uuid;

    public Lobby(Class<? extends Game> group, GameMap map) {
        this.group = group;
        this.map = map;
        this.uuid = UUID.randomUUID();
    }

    /**
     * Loads the world representing this lobby. It will do nothing if it's already loaded.
     * @return the loaded world
     */
    public CompletableFuture<World> load() {
        CompletableFuture<World> future = new CompletableFuture<>();
        World world = Bukkit.getWorld(getWorldName());
        if (world != null) {
            future.complete(world);
        } else {
            try {
                future.complete(
                        new WorldCreator(getWorldName())
                                .type(WorldType.FLAT)
                                .generator("VoidGen")
                                .keepSpawnLoaded(TriState.FALSE)
                                .createWorld()
                );
            } catch (Exception ex) {
                future.obtrudeException(ex);
            }
        }
        return future;
    }

    /**
     * Summons a player into this lobby. The world will be loaded if not beforehand.
     * @param gamePlayer the player
     */
    public void summon(GamePlayer gamePlayer) {
        load().thenAccept(world -> SimmyGameAPI.scheduler().runTask(() -> {
            if (gamePlayer.asBukkitPlayer() != null)
                gamePlayer.teleport(world.getSpawnLocation());
        }));
    }

    /**
     * @return the game group representing this lobby
     */
    public Class<? extends Game> getGroup() {
        return group;
    }

    public GameMap getMap() {
        return map;
    }

    /**
     * @return the world name representing this lobby
     */
    public String getWorldName() {
        return "lobby-" + group.getSimpleName().toLowerCase(Locale.ENGLISH) + "-" + uuid;
    }

    public UUID getUuid() {
        return uuid;
    }

}
