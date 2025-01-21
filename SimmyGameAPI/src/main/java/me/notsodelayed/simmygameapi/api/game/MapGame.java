package me.notsodelayed.simmygameapi.api.game;

import java.io.File;
import java.io.IOException;
import java.util.Locale;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.atomic.AtomicLong;
import java.util.logging.Level;

import com.google.common.base.Preconditions;
import org.apache.commons.io.FileUtils;
import org.bukkit.*;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

import me.notsodelayed.simmygameapi.SimmyGameAPI;
import me.notsodelayed.simmygameapi.api.map.GameMap;
import me.notsodelayed.simmygameapi.api.map.GameMapManager;
import me.notsodelayed.simmygameapi.util.LoggerUtil;
import me.notsodelayed.simmygameapi.util.PlayerUtil;
import me.notsodelayed.simmygameapi.util.Util;

public abstract class MapGame<M extends GameMap> extends Game {

    private M map, queriedMap;
    private boolean lockMap = false;
    private final File worldDirectory;
    private World world;
    private final String worldName;
    private boolean setupCountdown = true;

    /**
     * @param minPlayers the minimum player count
     * @param maxPlayers the maximum player count
     * @apiNote This returns a Game instance with state {@link GameState#LOADING}, where it is not joinable.
     * @implSpec For custom game setup requirements, developers must override {@link #ready()} and call super before custom implementations.
     */
    protected MapGame(int minPlayers, int maxPlayers) {
        super(minPlayers, maxPlayers);
        this.worldName = this.getClass().getSimpleName().toLowerCase(Locale.ENGLISH) + "-" + getUuid();
        this.worldDirectory = new File(worldName);
    }

    /**
     * @param minPlayers the minimum player count
     * @param maxPlayers the maximum player count
     * @param map the map
     * @apiNote This returns a Game instance with state {@link GameState#LOADING}, where it is not joinable.
     * @implSpec For custom game setup requirements, developers must override {@link #ready()} and call super before custom implementations.
     */
    protected MapGame(int minPlayers, int maxPlayers, M map) {
        this(minPlayers, maxPlayers);
        this.map = map;
    }

    @Override
    protected boolean validate() {
        if (map == null && queriedMap == null && getMapManager().size() == 0) {
            SimmyGameAPI.logger.warning(getFormattedName() + " does not have a map, queried map, and idling maps in map manager.");
            dispatchPrefixedMessage("The game could not be started due to no maps available.");
            return false;
        }
        if (setupCountdown) {
            // TODO make this voteable
            getCountdown().executeAt(10, seconds -> {
                if (map == null) {
                    if (queriedMap != null) {
                        map = queriedMap;
                    } else {
                        map = getMapManager().randomChoices(1).getFirst();
                    }
                }
                lockMap();
                // TODO fix prefix trailing color
                dispatchPrefixedMessage("Map of the game: <gold>" + map.getDisplayName().orElse(map.getId()));
            });
            setupCountdown = false;
        }
        return super.validate();
    }

    /**
     * Overridden: to create a world of this map, before proceeding with the game.
     * @implNote Developers must call super from this class before custom implementations.
     */
    @Override
    public void init() {
        AtomicLong started = new AtomicLong();
        CompletableFuture.runAsync(() -> {
           try {
               started.set(System.currentTimeMillis());
               FileUtils.copyDirectory(getMap().getDirectory(), worldDirectory);
               worldDirectory.deleteOnExit();
           } catch (IOException ex) {
               dispatchMessage("<red>(!) An error occurred whilst setting up the map. This game has been terminated.");
               LoggerUtil.verbose(this, "An error occurred whilst copying " + map.getDisplayName().orElse(map.getId()) + ":");
               ex.printStackTrace(System.err);
               delete();
           }
        }).thenRun(() -> {
            // World loading must be synced
            // TODO modify gamerules, disable raids etc
            SimmyGameAPI.scheduler().runTask(() -> {
                world = new WorldCreator(worldName)
                        .type(WorldType.FLAT)
                        .generator("VoidGen")
                        .generateStructures(false)
                        .createWorld();
                long timeTaken = started.get() - System.currentTimeMillis();
                if (world == null) {
                    dispatchMessage("<red>(!) An error occurred whilst setting up the map. This game has been terminated.");
                    LoggerUtil.verbose(this, "Game world of map '" + map.getDisplayName().orElse(map.getId()) + "' is null after creation");
                    delete();
                    return;
                }
                getPlayers().forEach(gamePlayer -> PlayerUtil.clean(gamePlayer, GameMode.SURVIVAL));
                spawnPlayers();
                if (timeTaken > 50) {
                    LoggerUtil.verbose(this, String.format("Map '%s' took %s ms to load", map.getDisplayName().orElse(map.getId()), timeTaken), Level.WARNING, true);
                }
            });
        });
    }

    /**
     * Deletes the game with the game world.
     */
    @Override
    protected void delete() {
        super.delete();
        if (world != null) {
                world.getPlayers().forEach(player -> {
                    player.teleportAsync(Util.getMainWorld().getSpawnLocation());
                });
            Bukkit.unloadWorld(world, false);
        }
        getPlayers().forEach(gamePlayer -> PlayerUtil.clean(gamePlayer, GameMode.ADVENTURE));
        SimmyGameAPI.scheduler().runTaskAsynchronously(() -> {
            try {
                FileUtils.deleteDirectory(worldDirectory);
            } catch (IOException ex) {
                LoggerUtil.verbose(this, "Failed to delete the world directory. It will be removed on JVM shutdown.", Level.WARNING, true);
            }
        });
    }

    /**
     * Teleports the game players into the game world.
     * @throws IllegalStateException if the game world is not loaded
     */
    public void spawnPlayers() {
        if (world == null)
            throw new IllegalStateException("game world is not loaded");
        getPlayers().forEach(gamePlayer -> {
            Player player = gamePlayer.getPlayer();
            if (player == null)
                return;
            player.teleportAsync(world.getSpawnLocation());
        });
    }

    public abstract @NotNull GameMapManager<M> getMapManager();

    /**
     * Locks the map of this game from changing.
     */
    protected void lockMap() {
        lockMap = true;
    }

    /**
     * Unlocks the map of this game from changing.
     */
    protected void unlockMap() {
        lockMap = false;
    }

    public M getQueriedMap() {
        return queriedMap;
    }

    public void setQueriedMap(M queriedMap) {
        this.queriedMap = queriedMap;
    }

    /**
     * Applies {@link #getQueriedMap()} to {@link #getMap()}.
     * @return whether the map is successfully applied
     */
    public boolean applyQueriedMap() {
        if (lockMap)
            return false;
        map = queriedMap;
        queriedMap = null;
        return true;
    }

    /**
     * @return the map to be used
     */
    public M getMap() {
        return map;
    }

    /**
     * @param map the map to be used
     * @throws IllegalStateException if the game has already locked in the map to be used
     */
    protected void setMap(M map) {
        checkMapLock();
        this.map = map;
    }

    /**
     * @return the world of this game
     * @throws IllegalStateException if this game has not begun, as its world is not loaded yet
     * @see #getGameWorldName()
     */
    public @NotNull World getWorld() {
        return world;
    }

    public String getGameWorldName() {
        return worldName;
    }

    /**
     * @throws IllegalStateException if the map is locked from modifications
     */
    protected void checkMapLock() {
        Preconditions.checkState(!lockMap, "map is locked from modifications");
    }

}
