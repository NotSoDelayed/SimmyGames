package me.notsodelayed.simmygameapi.api.game;

import java.io.File;
import java.io.IOException;
import java.time.Instant;
import java.util.HashSet;
import java.util.Objects;
import java.util.Set;
import java.util.UUID;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.logging.Level;
import java.util.stream.Collectors;

import me.notsodelayed.simmygameapi.SimmyGameAPI;
import me.notsodelayed.simmygameapi.api.entity.GamePlayer;
import me.notsodelayed.simmygameapi.api.event.game.GameStartCountdownEvent;
import me.notsodelayed.simmygameapi.api.kit.GameKit;
import me.notsodelayed.simmygameapi.api.map.GameMap;
import me.notsodelayed.simmygameapi.api.map.MapChoice;
import me.notsodelayed.simmygameapi.util.LoggerUtil;
import me.notsodelayed.simmygameapi.util.PlayerUtil;
import me.notsodelayed.simmygameapi.util.StringUtil;
import me.notsodelayed.simmygameapi.util.Util;
import org.apache.commons.io.FileUtils;
import org.bukkit.Bukkit;
import org.bukkit.GameMode;
import org.bukkit.OfflinePlayer;
import org.bukkit.Sound;
import org.bukkit.SoundCategory;
import org.bukkit.World;
import org.bukkit.WorldCreator;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitTask;
import org.jetbrains.annotations.ApiStatus;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

/**
 * Represents a game.
 */
public abstract class Game<P extends GamePlayer> {

    private final UUID uuid;
    protected final Set<P> players = new HashSet<>();
    private GameState gameState = GameState.LOADING;
    private final Settings settings;
    private final MapChoice mapChoice;
    private GameMap gameMap;
    @Nullable
    private GameMap queriedGameMap;
    @Nullable
    private World gameWorld;
    private Instant started;
    private BukkitTask gameStartTask;
    private boolean force;

    /**
     * Represents a {@link Game} settings.
     * @apiNote Usage of setters are only allowed while <b>{@link Game#hasBegun()} = true</b>
     */
    @SuppressWarnings("unused")
    public static class Settings {

        private final Game<? extends GamePlayer> game;
        private int endIn = 20;
        private int minPlayers, maxPlayers;
        private int setupTimeout = 1;
        private int startIn = 30;
        private boolean startWithMinimumPlayers = true;

        private Settings(Game<? extends GamePlayer> game) {
            this.game = game;
        }

        /**
         * @return whether the game should start automatically whenever {@link #hasMinimumPlayers()} = true
         */
        public boolean startWithMinimumPlayers() {
            return startWithMinimumPlayers;
        }

        /**
         * @param startWithMinimumPlayers whether the game should start automatically whenever {@link #hasMinimumPlayers()} = true
         * @return this instance, for chaining
         */
        public Settings startWithMinimumPlayers(boolean startWithMinimumPlayers) {
            this.startWithMinimumPlayers = startWithMinimumPlayers;
            return this;
        }

        /**
         * @return the game end warmup, in positive seconds
         */
        public int endIn() {
            return endIn;
        }

        /**
         * @param endIn the game end warmup, in positive seconds
         * @return this instance, for chaining
         */
        public Settings endIn(int endIn) {
            game.requireState(GameState.LOADING, GameState.WAITING, GameState.STARTING);
            this.endIn = endIn;
            return this;
        }

        /**
         * @return the game minimum players
         */
        public int minPlayers() {
            return minPlayers;
        }
        /**
         * @param minPlayers the game minimum players, where minPlayers <= maxPlayers
         * @return this instance, for chaining
         */
        public Settings minPlayers(int minPlayers) {
            game.requireState(GameState.LOADING, GameState.WAITING, GameState.STARTING);
            this.minPlayers = Math.min(minPlayers, maxPlayers);
            return this;
        }

        /**
         * @return the game maximum players
         */
        public int maxPlayers() {
            return maxPlayers;
        }

        /**
         * @param maxPlayers the game maximum players, where maxPlayers >= minPlayers
         * @return this instance, for chaining
         */
        public Settings maxPlayers(int maxPlayers) {
            game.requireState(GameState.LOADING, GameState.WAITING, GameState.STARTING);
            this.maxPlayers = Math.max(maxPlayers, minPlayers);
            return this;
        }

        /**
         * @return the post-game setup timeout, in positive seconds
         */
        public int setupTimeout() {
            return setupTimeout;
        }

        /**
         * @param setupTimeout the post-game setup timeout, in positive seconds
         * @return this instance, for chaining
         */
        public Settings setupTimeout(int setupTimeout) {
            game.requireState(GameState.LOADING, GameState.WAITING, GameState.STARTING);
            this.setupTimeout = Math.max(setupTimeout, 0);
            return this;
        }

        /**
         * @return the game start warmup, in seconds, where seconds >= 10
         */
        public int startIn() {
            return startIn;
        }

        /**
         * @param startIn the game start warmup, in seconds, where seconds >= 10
         * @return this instance, for chaining
         */
        public Settings startIn(int startIn) {
            game.requireState(GameState.LOADING, GameState.WAITING, GameState.STARTING);
            this.startIn = Math.max(startIn, 10);
            return this;
        }

        @Override
        public String toString() {
            return "Settings{" +
                    "game=" + game +
                    ", endIn=" + endIn +
                    ", minPlayers=" + minPlayers +
                    ", maxPlayers=" + maxPlayers +
                    ", setupTimeout=" + setupTimeout +
                    ", startIn=" + startIn +
                    ", startWithMinimumPlayers=" + startWithMinimumPlayers +
                    '}';
        }
    }

    /**
     * @param minPlayers the minimum player count
     * @param maxPlayers the maximum player count
     * @param queriedGameMap the game map to set before game officially starts
     * @apiNote This returns a Game instance with state {@link GameState#LOADING}, where it is not joinable. <p>Developers must call {@link #ready()} in post-setup. </p>
     */
    protected Game(int minPlayers, int maxPlayers, @NotNull GameMap queriedGameMap) {
        this.uuid = UUID.randomUUID();
        this.settings = new Settings(this)
                .maxPlayers(maxPlayers)
                .minPlayers(minPlayers);
        this.queriedGameMap = queriedGameMap;
        this.mapChoice = new MapChoice(null);
    }

    /**
     * @param minPlayers the minimum player count
     * @param maxPlayers the maximum player count
     * @param mapChoice the map choices for in-game players to vote for
     */
    protected Game(int minPlayers, int maxPlayers, @NotNull MapChoice mapChoice) {
        this.uuid = UUID.randomUUID();
        this.settings = new Settings(this)
                .minPlayers(minPlayers)
                .maxPlayers(maxPlayers);
        this.queriedGameMap = null;
        this.mapChoice = mapChoice;
    }

    /**
     * @param message the action bar message to dispatch to all game players
     */
    public void dispatchActionBar(String message) {
        for (Player player : getBukkitPlayers()) {
            player.sendActionBar(StringUtil.color(message));
        }
    }

    /**
     * @param message the message to dispatch to all game players
     */
    public void dispatchMessage(String message) {
        for (Player player : getBukkitPlayers()) {
            player.sendMessage(StringUtil.color(message));
        }
    }

    /**
     * @param sound the sound to dispatch to all game players
     * @param pitch the sound pitch
     */
    public void dispatchSound(Sound sound, int pitch) {
        for (Player player : getBukkitPlayers()) {
            player.playSound(player, sound, SoundCategory.RECORDS, 2, pitch);
        }
    }

    /**
     * Marks this game as ready for joining.
     * @return whether this operation is a success
     */
    public boolean ready() {
        if (gameState == GameState.LOADING) {
            gameState = GameState.WAITING;
            return true;
        }
        return false;
    }

    /**
     * Triggers the game start sequence after verifying game requirements (i.e. minimum player count).
     * @return whether this request is approved
     */
    public boolean start(GameStartCountdownEvent.StartCause startCause) {
        return start(settings.startIn, startCause, false);
    }

    /**
     * Triggers the game start sequence.
     * <p>If <b>force</b> == true, cancelling {@link GameStartCountdownEvent} does not cancel this operation.</p>
     * @param force whether to ignore game requirements (i.e. minimum player count)
     * @return whether this request is approved
     */
    public boolean start(GameStartCountdownEvent.StartCause startCause, boolean force) {
        return start(settings.startIn, startCause, force);
    }

    /**
     * Triggers the game start sequence.
     * <p>If <b>force</b> == true, cancelling {@link GameStartCountdownEvent} does not cancel this operation.</p>
     * @param force whether to ignore game requirements (i.e. minimum player count)
     * @return whether this operation is successful
     */
    public boolean start(int startIn, GameStartCountdownEvent.StartCause startCause, boolean force) {
        if (gameState != GameState.WAITING)
            return false;
        LoggerUtil.verbose(this, "Game called to start (force: " + force + ")");
        if (!force && !this.hasMinimumPlayers())
            return false;
        if (!new GameStartCountdownEvent(this, startCause).callEvent() && !force) {
            LoggerUtil.verbose(this, "Game start countdown cancelled due to event cancellation");
            return false;
        }
        this.force = force;
        gameState = GameState.STARTING;
        AtomicInteger seconds = new AtomicInteger(startIn);
        Bukkit.getScheduler().runTaskTimer(SimmyGameAPI.instance, countdown -> {
            gameStartTask = countdown;
            if (seconds.get() == 0) {
                dispatchMessage("Game start message");
                try {
                    gameState = GameState.INGAME;
                    started = Instant.now();
                    this.createGameWorld().get();
                    AtomicInteger timeout = new AtomicInteger();
                    Bukkit.getScheduler().runTaskTimer(SimmyGameAPI.instance, pending -> {
                        if (Bukkit.getWorld("game-" + uuid) != null) {
                            for (GamePlayer gamePlayer : this.getPlayers()) {
                                assert getGameWorld() != null;
                                gamePlayer.spawn();
                                PlayerUtil.clean(gamePlayer, GameMode.SURVIVAL);
                            }
                            this.tick();
                            pending.cancel();
                            return;
                        }
                        if (timeout.getAndIncrement() == settings.setupTimeout) {
                            pending.cancel();
                            LoggerUtil.verbose(this, "A post-start process took too long to complete. This game has been terminated.", Level.SEVERE, true);
                            dispatchMessage("&cThis game has been forcibly terminated due to a post-start setup error. Please report this to the server administrator!");
                            this.delete();
                        }
                    }, 1, 1);
                } catch (Exception ex) {
                    LoggerUtil.verbose(this, "An exception occurred during the game world creation process. This game has been terminated. Refer to the stacktrace below for more info:", Level.SEVERE, true);
                    dispatchMessage("&cThis game has been forcibly terminated due to a post-start setup error. Please report this to the server administrator!");
                    Bukkit.getScheduler().runTask(SimmyGameAPI.instance, this::delete);
                    throw new RuntimeException(ex);
                } finally {
                    countdown.cancel();
                }
                return;
            }
            if (seconds.get() == startIn || seconds.get() % 10 == 0 || seconds.get() <= 5) {
                // TODO adapt to ingame map voting
                if (seconds.get() == 10 && gameMap == null) {
                    if (queriedGameMap == null && !mapChoice.isEmpty()) {
                        gameMap = mapChoice.getRandom();
                    } else if (queriedGameMap != null) {
                        gameMap = queriedGameMap;
                    }
                    if (gameMap == null) {
                        countdown.cancel();
                        dispatchMessage("&cThe game could not be started due to no maps available. Please report this to the server administrator!");
                        LoggerUtil.verbose(this, "Game could not be started! Refer to the stacktrace below for more info:", Level.SEVERE, true);
                        throw new RuntimeException("MapChoice and queriedGameMap are empty and null respectively");
                    }
                    LoggerUtil.verbose(this, "Game map automatically set to: " + gameMap.getOptionalDisplayName().orElse(gameMap.getId()));
                }
                dispatchMessage("&eGame will start in " + seconds.get() + "...");
                dispatchSound(Sound.BLOCK_NOTE_BLOCK_HAT, 1);
            }
            seconds.getAndDecrement();
        }, 0, 20);
        return true;
    }

    /**
     * Attempts to cancel the game start countdown task. Ineffective to forced starts.
     */
    public void cancelGameStartTask(@NotNull String reason) {
        if (gameStartTask != null && !force) {
            gameStartTask.cancel();
            this.dispatchMessage("&eGame start aborted: " + reason);
        }
    }

    /**
     * Called after the game countdown depletes.
     * @apiNote Developers shall override this method to commence the game procedure.
     */
    protected abstract void tick();

    /**
     * Ends the game. Only effective if {@link Game#hasBegun()} == true.
     * @param tie whether to end the game as a tie
     */
    public void end(boolean tie) {
        if (!this.hasBegun())
            return;
        LoggerUtil.verbose(this, "Game has ended with winner: tied " + tie);
        gameState = GameState.ENDING;

        // TODO literal shit for checking the winner team if tie = false

        for (Player bukkitPlayer : getBukkitPlayers()) {
            bukkitPlayer.setGameMode(GameMode.ADVENTURE);
            bukkitPlayer.getInventory().clear();
            bukkitPlayer.clearActivePotionEffects();
        }
        AtomicInteger seconds = new AtomicInteger(20);
        Bukkit.getScheduler().runTaskTimer(SimmyGameAPI.instance, task -> {
            dispatchActionBar("Game shutdown in " + seconds.get() + "...");
            if (seconds.get() == 0) {
                Bukkit.getScheduler().runTask(SimmyGameAPI.instance, this::delete);
                task.cancel();
            }
            seconds.getAndDecrement();
        }, 0, 20);
    }

    /**
     * Deletes the game.
     */
    protected void delete() {
        LoggerUtil.verbose(this, "Deleting...");
        gameState = GameState.DISABLED;
        if (this.getGameWorld() != null)
            for (Player player : this.getGameWorld().getPlayers()) {
                player.teleport(Util.getMainWorld().getSpawnLocation());
                PlayerUtil.clean(player, GameMode.ADVENTURE);
            }
        // Run on next tick
        Bukkit.getScheduler().runTask(SimmyGameAPI.instance, () -> {
            File worldDirectory = new File(this.getGameWorld().getName());
            Bukkit.unloadWorld(this.getGameWorld(), false);
            try {
                FileUtils.deleteDirectory(worldDirectory);
            } catch (IOException e) {
                LoggerUtil.verbose(this, "Failed to delete game world! It will be automatically deleted upon server shutdown. Refer to the stacktrace below for more info:", Level.WARNING, true);
                e.printStackTrace(System.err);
            }
        });
    }

    /**
     * @throws IllegalStateException if the game is not joinable
     * @see GamePlayer#GamePlayer(Player, Game, GameKit)
     * @see #removePlayer(GamePlayer)
     */
    @ApiStatus.Internal
    public void addPlayer(P gamePlayer) {
        if (gamePlayer.getGame() != this) {
            LoggerUtil.expect(gamePlayer.getGame(), this);
            throw new IllegalArgumentException("GamePlayer game mismatch");
        }
        if (!this.isJoinable())
            throw new IllegalStateException("GamePlayer add with game in unjoinable state");
        players.add(gamePlayer);
        this.dispatchMessage("&e" + gamePlayer.getName() + " &ejoined! (" + this.getPlayers().size() + "/" + this.getSettings().maxPlayers() + ")");
        if (this.hasMinimumPlayers())
            this.start(GameStartCountdownEvent.StartCause.GAME_REQUIREMENTS_MET);
    }

    /**
     * @param gamePlayer the game player
     * @return the player associated
     */
    public Player removePlayer(P gamePlayer) {
        players.remove(gamePlayer);
        this.dispatchMessage("&e" + gamePlayer.getName() + " &eleft! (" + this.getPlayers().size() + "/" + this.getSettings().maxPlayers() + ")");
        return gamePlayer.asBukkitPlayer().getPlayer();
    }

    /**
     * @return whether the minimum player requirements for this game has met
     */
    public boolean hasMinimumPlayers() {
        return players.size() >= settings.minPlayers;
    }

    /**
     * @return whether this game has begun
     */
    public boolean hasBegun() {
        return gameState != GameState.WAITING && gameState != GameState.STARTING;
    }

    /**
     * @return whether this game is about to end, or has ended
     */
    public boolean hasEnded() {
        return gameState == GameState.ENDING || gameState == GameState.DISABLED;
    }

    /**
     * @return whether this game is joinable
     */
    public boolean isJoinable() {
        return Util.equalsAny(gameState, GameState.WAITING, GameState.STARTING, GameState.INGAME);
    }

    /**
     * @return whether this game is about to start (game start countdown is ticking)
     */
    public boolean isAboutToStart() {
        return gameState == GameState.STARTING;
    }

    /**
     * @return the queried game map
     */
    @Nullable
    public GameMap getQueriedGameMap() {
        return queriedGameMap;
    }

    /**
     * @param gameMap the queried game map
     */
    public void setQueriedGameMap(@Nullable GameMap gameMap) {
        queriedGameMap = gameMap;
    }

    /**
     * @return the game map, or null if not defined yet
     */
    @Nullable
    public GameMap getGameMap() {
        return gameMap;
    }

    /**
     * @param gameMap the game map
     */
    protected void setGameMap(@NotNull GameMap gameMap) {
        this.gameMap = gameMap;
    }

    /**
     * Creates the game world. Usually called before {@link Game#tick()}.
     * @see Game#setQueriedGameMap(GameMap)
     */
    protected CompletableFuture<World> createGameWorld() {
        File destination = new File("game-" + uuid);
        destination.deleteOnExit();
        return CompletableFuture.supplyAsync(() -> {
            try {
                FileUtils.copyDirectory(gameMap.getDirectory(), destination);
            } catch (IOException e) {
                LoggerUtil.verbose(this, "An exception occurred whilst creating the game world with map " + gameMap.getOptionalDisplayName().orElse(gameMap.getId()) + ". The game has been forcibly terminated. Refer to below stacktrace for more info:");
                this.dispatchMessage("&cThis game has been forcibly terminated due to failure of creating the game world! Please report this to the server administrator.");
                this.delete();
                throw new RuntimeException(e);
            }
            return destination.toString();
        }).thenApply(worldName -> {
            Bukkit.getScheduler().runTask(SimmyGameAPI.instance, () -> gameWorld = new WorldCreator(destination.getName())
                    .generator("VoidGen:{}")
                    .createWorld());
            return gameWorld;
        });
    }

    /**
     * @return the Bukkit world, or null if it is not loaded due to {@link Game#getGameMap()} is null
     */
    @Nullable
    public World getGameWorld() {
        return gameWorld;
    }

    /**
     * @return the game state
     */
    public GameState getGameState() {
        return gameState;
    }

    /**
     * @param gameState the game state
     */
    protected void setGameState(@NotNull GameState gameState) {
        this.gameState = gameState;
    }

    /**
     * @return the map choice
     * @apiNote the map choice is never null, but it may house no maps for picking
     */
    @NotNull
    public MapChoice getMapChoice() {
        return mapChoice;
    }

    /**
     * @return an immutable set of game players
     */
    public Set<P> getPlayers() {
        return Set.copyOf(players);
    }

    /**
     * Time-saving method for mapping {@link GamePlayer} instances to {@link Player}.
     * @return an immutable set of {@link Player}
     */
    public Set<Player> getBukkitPlayers() {
        return players.stream()
                .map(GamePlayer::asBukkitPlayer)
                .map(OfflinePlayer::getPlayer)
                .filter(Objects::nonNull)
                .collect(Collectors.toUnmodifiableSet());
    }

    /**
     * @return the game uuid
     */
    public UUID getUuid() {
        return uuid;
    }

    /**
     * @return the display uuid (first portion of a {@link UUID})
     */
    public String getDisplayUuid() {
        return uuid.toString().split("-")[0];
    }

    /**
     * @return the game settings
     */
    public Settings getSettings() {
        return settings;
    }

    @Override
    public String toString() {
        return "Game{" +
                "settings={" + settings + '}' +
                ", uuid=" + uuid +
                ", players=" + players +
                ", gameState=" + gameState +
                ", gameMap=" + gameMap +
                ", queriedGameMap=" + queriedGameMap +
                ", gameWorld=" + gameWorld +
                '}';
    }

    /**
     * Checks for whether the given game state matches the current state of the game during code execution in runtime, and throws an {@link IllegalStateException} if so.
     * @param requiredStates the illegal state
     * @throws IllegalStateException if any of the provided state matches the current state during code execution
     */
    protected void requireState(GameState... requiredStates) {
        for (GameState requiredState : requiredStates) {
            if (gameState == requiredState)
                return;
        }
        throw new IllegalStateException("illegal execution for " + uuid + " with state " + gameState);
    }

}
