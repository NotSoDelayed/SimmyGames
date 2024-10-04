package me.notsodelayed.simmygameapi.api.game;

import java.io.File;
import java.io.IOException;
import java.util.Locale;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.atomic.AtomicLong;
import java.util.logging.Level;

import com.google.common.base.Preconditions;
import org.apache.commons.io.FileUtils;
import org.bukkit.Bukkit;
import org.bukkit.World;
import org.bukkit.WorldCreator;
import org.bukkit.WorldType;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

import me.notsodelayed.simmygameapi.SimmyGameAPI;
import me.notsodelayed.simmygameapi.api.game.map.GameMap;
import me.notsodelayed.simmygameapi.util.LoggerUtil;
import me.notsodelayed.simmygameapi.util.Util;

public abstract class MapGame<M extends GameMap> extends Game {

    private M map, queriedMap;
    private boolean lockMap = false;
    private File worldDirectory;
    private World world;
    private final String worldName;

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
     * Overridden: to create a world of this map, before proceeding with the game.
     * @implNote Developers must call super from this class before custom implementations.
     */
    @Override
    public void tick() {
        long started = System.currentTimeMillis();
        AtomicLong aTimeTakenCopy = new AtomicLong();
        CompletableFuture.runAsync(() -> {
           try {
               FileUtils.copyDirectory(getMap().getDirectory(), worldDirectory);
               worldDirectory.deleteOnExit();
               aTimeTakenCopy.set(System.currentTimeMillis());
           } catch (IOException ex) {
               dispatchMessage("&c(!) An error occurred whilst setting up the map. This game has been terminated.");
               LoggerUtil.verbose(this, "An error occurred whilst copying " + map.getDisplayName().orElse(map.getId()) + ":");
               ex.printStackTrace(System.err);
               delete();
           }
        });
        long timeTakenCopy = aTimeTakenCopy.get() - started;

        started = System.currentTimeMillis();
        world = new WorldCreator(worldName)
                .type(WorldType.FLAT)
                .generator("VoidGen:{}")
                .generateStructures(false)
                .createWorld();
        long timeTakenLoad = System.currentTimeMillis() - started;
        if (world == null)
            throw new RuntimeException("world is null after world creation");
        if (timeTakenCopy + timeTakenLoad > 50) {
            LoggerUtil.verbose(this, String.format("Map '%s' took longer than 1 tick to load (%sms copy, %sms load)", map.getDisplayName().orElse(map.getId()), timeTakenCopy, timeTakenLoad), Level.WARNING, true);
        }
    }

    /**
     * Deletes the game with the game world.
     */
    @Override
    protected void delete() {
        super.delete();
        if (world != null) {
            for (Player player : world.getPlayers())
                player.teleport(Util.getMainWorld().getSpawnLocation());
            Bukkit.unloadWorld(world, false);
        }
        Bukkit.getScheduler().runTaskAsynchronously(SimmyGameAPI.instance, () -> {
            try {
                FileUtils.deleteDirectory(worldDirectory);
            } catch (IOException ex) {
                LoggerUtil.verbose(this, "Failed to delete the world directory. It will be removed on JVM shutdown.", Level.WARNING, true);
            }
        });
    }

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
     * @throws IllegalStateException if the map is locked from modifications
     */
    public void applyQueriedMap() {
        checkMapLock();
        map = queriedMap;
        queriedMap = null;
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
    public void setMap(M map) {
        checkMapLock();
        this.map = map;
    }

    /**
     * @return the world of this game
     * @throws IllegalStateException if this game has not begun, as its world is not loaded yet
     * @see #getWorldName()
     */
    public @NotNull World getWorld() {
        return world;
    }

    public String getWorldName() {
        return worldName;
    }

    /**
     * @throws IllegalStateException if the map is locked from modifications
     */
    protected void checkMapLock() {
        Preconditions.checkState(!lockMap, "map is locked from modifications");
    }

}
