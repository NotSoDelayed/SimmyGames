package me.notsodelayed.simmygameapi.api.game;

import java.io.File;
import java.io.IOException;
import java.util.HashSet;
import java.util.Locale;
import java.util.Set;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.atomic.AtomicLong;
import java.util.function.Consumer;
import java.util.logging.Level;
import java.util.stream.Collectors;

import com.google.common.base.Preconditions;
import org.apache.commons.io.FileUtils;
import org.bukkit.*;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import me.notsodelayed.simmygameapi.SimmyGameAPI;
import me.notsodelayed.simmygameapi.api.Game;
import me.notsodelayed.simmygameapi.api.GameMap;
import me.notsodelayed.simmygameapi.api.GamePlayer;
import me.notsodelayed.simmygameapi.api.feature.Feature;
import me.notsodelayed.simmygameapi.api.map.FixedMap;
import me.notsodelayed.simmygameapi.api.map.GameMapManager;
import me.notsodelayed.simmygameapi.api.map.NaturalMap;
import me.notsodelayed.simmygameapi.util.LoggerUtil;
import me.notsodelayed.simmygameapi.util.PlayerUtil;
import me.notsodelayed.simmygameapi.util.Util;

public abstract class MapGame<M extends GameMap> extends Game {

    private static final Set<MapGame<? extends GameMap>> MAP_GAMES = new HashSet<>();
    private M map, queriedMap;
    private boolean lockMap = false;
    private final String worldName;
    private final File worldDirectory;
    private World world;
    private final Set<Feature> features = new HashSet<>();

    protected MapGame(int minPlayers, int maxPlayers) {
        super(minPlayers, maxPlayers);
        this.worldName = this.getClass().getSimpleName().toLowerCase(Locale.ENGLISH) + "-" + getUuid();
        this.worldDirectory = new File(worldName);
        MAP_GAMES.add(this);
        getCountdown().executeAt(10, seconds -> {
            if (map == null) {
                if (queriedMap != null) {
                    map = queriedMap;
                } else {
                    map = getMapManager().randomChoices(1).getFirst();
                }
            }
            lockMap();
            dispatchPrefixedMessage("Map of the game: <gold>" + map.displayNameOrId());
        });
    }

    /**
     * @param minPlayers the minimum player count
     * @param maxPlayers the maximum player count
     * @param map the map to play
     */
    protected MapGame(int minPlayers, int maxPlayers, M map) {
        this(minPlayers, maxPlayers);
        this.map = map;
    }

    /**
     * @param world the world
     * @return the {@link MapGame} which manages the provided world
     */
    public static @Nullable MapGame<? extends GameMap> getGame(World world) {
        return MAP_GAMES.stream()
                .filter(game -> game.getGameWorldName().equals(world.getName()))
                .findAny().orElse(null);
    }


    public Set<Class<? extends Feature>> features() {
        return features.stream()
                .map(Feature::getClass)
                .collect(Collectors.toUnmodifiableSet());
    }

    public boolean hasFeature(Class<? extends Feature> feature) {
        return features.stream().anyMatch(f -> f.getClass().equals(feature));
    }

    /**
     * Enables a {@link Feature} and register it for this game.
     * @param clazz the feature class
     * @param instance the provided feature to utilise
     */
    protected <T extends Feature> void enableFeature(Class<T> clazz, Consumer<T> instance) {
        T feature = Feature.newFeature(this, clazz);
        features.add(feature);
        instance.accept(feature);
    }

    @Override
    public boolean hasMetGameRequirements() {
        if (map == null && queriedMap == null && getMapManager().size() == 0) {
            SimmyGameAPI.logger.warning(getFormattedName() + " does not have a map, queried map, and idling maps in map manager.");
            dispatchPrefixedMessage("<red>The game could not be started due to no maps available.");
            return false;
        }
        return super.hasMetGameRequirements();
    }

    /**
     * @return the loaded game world of {@link #getMap()}
     */
    protected CompletableFuture<World> loadGameWorld() {
        if (world != null)
            return CompletableFuture.completedFuture(world);
        AtomicLong started = new AtomicLong();
        if (map instanceof FixedMap fixedMap) {
            CompletableFuture.runAsync(() -> {
                try {
                    started.set(System.currentTimeMillis());
                    FileUtils.copyDirectory(fixedMap.fileLocation(), worldDirectory);
                    worldDirectory.deleteOnExit();
                } catch (IOException ex) {
                    dispatchMessage("<red>(!) An error occurred whilst setting up the map. This game has been terminated.");
                    LoggerUtil.verbose(this, "An error occurred whilst copying " + map.displayNameOrId() + ":");
                    ex.printStackTrace(System.err);
                    delete();
                }
            }).join();
            world = new WorldCreator(worldName)
                    .type(WorldType.FLAT)
                    .generator("VoidGen:{}")
                    .generateStructures(false)
                    .createWorld();
        } else if (map instanceof NaturalMap naturalMap) {
            world = naturalMap.worldCreator().createWorld();
        } else {
            dispatchMessage("<red>(!) An error occurred whilst setting up the map. This game has been terminated.");
            LoggerUtil.verbose(this, String.format("Unknown map type from '%s' (type: %s)", map.displayNameOrId(), map.getClass().getSimpleName()));
            delete();
        }
        long timeTaken = started.get() - System.currentTimeMillis();
        if (world == null) {
            dispatchMessage("<red>(!) An error occurred whilst setting up the map. This game has been terminated.");
            LoggerUtil.verbose(this, "Game world of map '" + map.displayNameOrId() + "' is null after creation");
            delete();
            return CompletableFuture.failedFuture(new RuntimeException("world is null after creation"));
        }
        if (timeTaken > 50)
            LoggerUtil.verbose(this, String.format("Map '%s' took %s ms to load", map.displayNameOrId(), timeTaken), Level.WARNING, true);
        return CompletableFuture.completedFuture(world);
    }

    @Override
    protected void init() {
        loadGameWorld().thenAccept(world -> {
           SimmyGameAPI.scheduler().runTask(() -> {
               getPlayers().forEach(gamePlayer -> {
                   gamePlayer.teleport(world.getSpawnLocation());
                   PlayerUtil.reset(gamePlayer, GameMode.SURVIVAL);
               });
           });
        });
    }

    @Override
    public void onPlayerJoin(GamePlayer player) {
        if (hasBegun()) {
            player.teleport(world.getSpawnLocation());
            PlayerUtil.reset(player, GameMode.SURVIVAL);
        }
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
        getPlayers().forEach(gamePlayer -> PlayerUtil.reset(gamePlayer, GameMode.ADVENTURE));
        SimmyGameAPI.scheduler().runTaskAsynchronously(() -> {
            try {
                FileUtils.deleteDirectory(worldDirectory);
            } catch (IOException ex) {
                LoggerUtil.verbose(this, "Failed to delete the world directory. It will be removed on JVM shutdown.", Level.WARNING, true);
            }
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

    static {
        // TODO check why map game worlds are being leftover
        Runtime.getRuntime().addShutdownHook(new Thread(() -> {
            Game.getGames().values().forEach(game -> {
                if (game instanceof MapGame<?> mapGame) {
                    SimmyGameAPI.logger.info("Deleting game world for " + mapGame.getFormattedName());
                    mapGame.delete();
                }
            });
        }));
    }

}
