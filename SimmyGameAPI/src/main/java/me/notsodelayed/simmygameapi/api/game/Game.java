package me.notsodelayed.simmygameapi.api.game;

import java.time.Instant;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Optional;
import java.util.Set;
import java.util.UUID;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.function.Consumer;
import java.util.stream.Collectors;

import com.google.common.base.Preconditions;
import net.md_5.bungee.api.ChatColor;
import org.bukkit.Bukkit;
import org.bukkit.GameMode;
import org.bukkit.Sound;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.scheduler.BukkitTask;
import org.jetbrains.annotations.ApiStatus;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import me.notsodelayed.simmygameapi.SimmyGameAPI;
import me.notsodelayed.simmygameapi.api.game.event.GameStartCountdownEvent;
import me.notsodelayed.simmygameapi.api.game.player.GamePlayer;
import me.notsodelayed.simmygameapi.command.GameCommand;
import me.notsodelayed.simmygameapi.util.LoggerUtil;
import me.notsodelayed.simmygameapi.util.PlayerUtil;
import me.notsodelayed.simmygameapi.util.StringUtil;
import me.notsodelayed.simmygameapi.util.Util;

/**
 * Represents a game.
 */
public abstract class Game implements BaseGame {

    protected static final Map<UUID, Game> GAMES = new HashMap<>();
    private final UUID uuid;
    protected final Set<GamePlayer> players;
    protected GameState gameState = GameState.LOADING;
    private final Settings settings;
    @Nullable
    private String prefix = null;
    protected Instant started;
    protected BukkitTask gameStartTask;
    protected boolean force = false;
    protected Map<Integer, Consumer<Game>> countdownExecutable;

    /**
     * Represents a {@link Game} settings.
     * @apiNote Usage of setters are only allowed while <b>{@link Game#hasBegun()} = true</b>
     */
    @SuppressWarnings("unused")
    public class Settings {

        private final Game game;
        private int endIn = 20;
        private int startIn = 30;
        private int minPlayers, maxPlayers;
        private boolean startWithMinimumPlayers = true;

        private Settings(Game game) {
            this.game = game;
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
            Preconditions.checkState(isSafeForSettingsModification(), "game is not in idle states");
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
            Preconditions.checkState(isSafeForSettingsModification(), "game is not in idle states");
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
            Preconditions.checkState(isSafeForSettingsModification(), "game is not in idle states");
            this.maxPlayers = Math.max(maxPlayers, minPlayers);
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
            Preconditions.checkState(isSafeForSettingsModification(), "game is not in idle states");
            this.startIn = Math.max(startIn, 10);
            return this;
        }

        /**
         * @return whether this game should start automatically whenever {@link #hasMinimumPlayers()} = true
         */
        public boolean startWithMinimumPlayers() {
            return startWithMinimumPlayers;
        }

        /**
         * @param startWithMinimumPlayers whether this game should start automatically whenever {@link #hasMinimumPlayers()} = true
         * @return this instance, for chaining
         */
        public Settings startWithMinimumPlayers(boolean startWithMinimumPlayers) {
            this.startWithMinimumPlayers = startWithMinimumPlayers;
            return this;
        }

        @Override
        public String toString() {
            return "Settings{" +
                    "game=" + game +
                    ", endIn=" + endIn +
                    ", startIn=" + startIn +
                    ", minPlayers=" + minPlayers +
                    ", maxPlayers=" + maxPlayers +
                    ", startWithMinimumPlayers=" + startWithMinimumPlayers +
                    '}';
        }

    }

    /**
     * @param minPlayers the minimum player count
     * @param maxPlayers the maximum player count
     * @apiNote This returns a Game instance with state {@link GameState#LOADING}, where it is not joinable.
     * @implSpec For custom game setup requirements, developers must override {@link #ready()} and call super before custom implementations.
     */
    protected Game(int minPlayers, int maxPlayers) {
        this.uuid = UUID.randomUUID();
        this.players = new HashSet<>();
        this.settings = new Settings(this)
                .maxPlayers(maxPlayers)
                .minPlayers(minPlayers);
        GAMES.put(uuid, this);
        GameCommand.UUIDS_CACHE = null;
        countdownExecutable = new HashMap<>();
    }

    /**
     * @return an immutable copy of registered active games
     */
    public static Map<UUID, Game> getGames() {
        garbageCollection();
        return Map.copyOf(GAMES);
    }

    public void dispatchPrefixedMessage(String message) {
        dispatchMessage(prefix + "&r " + message);
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
     * @param sound the to dispatch to all game players
     * @param pitch the pitch
     */
    public void dispatchSound(Sound sound, float pitch) {
        dispatchSound(sound, 2, pitch);
    }

    /**
     * @param sound the sound to dispatch to all game players
     * @param volume the volume
     * @param pitch the pitch
     */
    public void dispatchSound(Sound sound, float volume, float pitch) {
        for (Player player : getBukkitPlayers()) {
            player.playSound(player.getLocation(), sound, volume, pitch);
        }
    }

    @Override
    public void ready() throws IllegalStateException {
        Preconditions.checkState(gameState == GameState.LOADING, "game is not in loading state");
        gameState = GameState.WAITING_FOR_PLAYERS;
    }

    /**
     * @param seconds the seconds mark for this task
     * @param task the task
     * @apiNote If there's an existing {@link Consumer} assigned, it will be overwritten.
     */
    public void executeTaskAtStartCountdown(int seconds, Consumer<Game> task) {
        countdownExecutable.put(seconds, task);
    }

    /**
     * @return whether this game's initialisation is successful
     * @apiNote Called after {@link #hasMetGameRequirements()}. In event of unhandled exception occurred in this method, it will return false.
     * @implNote Developers may implement custom initialisation tasks. Ensure this method returns <b>true</b> to allow the game start sequence to proceed.
     */
    protected abstract boolean init();

    /**
     * @return whether this game has its requirements met to start
     * @apiNote Forced starts are exempted.
     * @implNote Developers may override this to implement custom game requirements, and return <b>super</b> after.
     * @see #start(int, GameStartCountdownEvent.StartCause, boolean)
     */
    public boolean hasMetGameRequirements() {
        return hasMinimumPlayers();
    }

    /**
     * Triggers the game start sequence after verifying game requirements (i.e. minimum player count).
     * @see #init()
     */
    public void start(GameStartCountdownEvent.StartCause startCause) {
        start(settings.startIn(), startCause, false);
    }

    /**
     * Triggers the game start sequence.
     * <p>If <b>force</b> == true, cancelling {@link GameStartCountdownEvent} does not cancel this operation.</p>
     * @param force whether to ignore game requirements (i.e. minimum player count)
     * @see #init()
     */
    public void start(GameStartCountdownEvent.StartCause startCause, boolean force) {
        start(settings.startIn(), startCause, force);
    }

    /**
     * Triggers the game start sequence.
     * <p>If <b>force</b> == true, cancelling {@link GameStartCountdownEvent} does not cancel this operation.</p>
     * @param force whether to ignore game requirements (i.e. minimum player count)
     * @see #init()
     */
    public void start(int startIn, GameStartCountdownEvent.StartCause startCause, boolean force) {
        Preconditions.checkState(gameState == GameState.WAITING_FOR_PLAYERS, "game is not in waiting state");
        LoggerUtil.verbose(this, "Game called to start (force: " + force + ")");
        if (!force && !hasMetGameRequirements()) {
            LoggerUtil.verbose(this, "Game start countdown aborted due to game requirements not met.");
            return;
        }
        try {
            if (!init()) {
                LoggerUtil.verbose(this, "Game start countdown aborted due to init() returns false.");
                return;
            }
        } catch (Exception ex) {
            LoggerUtil.verbose(this, "Game start countdown aborted due to an exception occurred in init()");
            return;
        }
        this.force = force;
        gameState = GameState.STARTING;
        Bukkit.getPluginManager().callEvent(new GameStartCountdownEvent(this, startCause));
        AtomicInteger seconds = new AtomicInteger(startIn);
        Game game = this;
        gameStartTask = Bukkit.getScheduler().runTaskTimer(SimmyGameAPI.instance, new BukkitRunnable() {

            @Override
            public void run() {

                Consumer<Game> executable = countdownExecutable.get(seconds.get());
                if (executable != null)
                    executable.accept(game);

                if (seconds.get() == 0) {
                    gameState = GameState.INGAME;
                    started = Instant.now();
                    dispatchMessage("&aGame has started!");
                    tick();
                    gameStartTask.cancel();
                    gameStartTask = null;
                    return;
                }
                if (seconds.get() == startIn || seconds.get() % 10 == 0 || seconds.get() <= 5) {
                    dispatchMessage("&eGame will start in " + seconds.get() + "...");
                    dispatchSound(Sound.NOTE_STICKS, 1);
                }

                seconds.getAndDecrement();
            }
        }, 0, 20);
    }

    /**
     * Attempts to cancel the game start countdown task.
     */
    public void cancelGameStartTask(@NotNull String reason) {
        if (gameStartTask != null) {
            gameStartTask.cancel();
            gameStartTask = null;
            this.force = false;
            this.dispatchMessage(ChatColor.RED + reason);
            this.dispatchSound(Sound.NOTE_BASS, 1.5f);
        }
    }

    /**
     * Ends the game.
     * @see #delete()
     * @implNote The default implementation for ending a game. Subclasses may override this for custom implementation.
     */
    public void end() {
        end(gamePlayer -> {});
    }

    /**
     * Ends the game and schedules for deletion.
     * @param cleanup the cleanup task to perform on the game players
     * @see #delete()
     */
    public void end(Consumer<GamePlayer> cleanup) {
        end(cleanup, () -> {
            this.dispatchMessage("&eGame has ended!");
            AtomicInteger seconds = new AtomicInteger(20);
            Game game = this;
            Bukkit.getScheduler().runTaskTimer(SimmyGameAPI.instance, new BukkitRunnable() {
                @Override
                public void run() {
                    if (seconds.get() == 0) {
                        Bukkit.getScheduler().runTask(SimmyGameAPI.instance, game::delete);
                        this.cancel();
                    } else if (seconds.get() % 10 == 0) {
                        game.dispatchMessage(String.format("&eGame shutdown in %ss...", seconds.get()));
                    }
                    seconds.getAndDecrement();
                }
            }, 0, 20);
        });
    }

    /**
     * Ends the game.
     * @param cleanup the cleanup task to perform on the game players
     * @param postCleanup the post-cleanup task to perform on the game
     * @implNote Developers are <b>required</b> to call {@link #delete()} in <b>postCleanup</b> argument!
     * @see #delete()
     */
    public void end(Consumer<GamePlayer> cleanup, Runnable postCleanup) {
        if (!this.hasBegun())
            return;
        this.getPlayers().forEach(cleanup);
        postCleanup.run();
    }

    /**
     * Deletes the game.
     * @implNote Developers must call <b>super</b> before custom implementation!
     * @apiNote {@link #getPlayers()} returns an empty set after this method execution.
     */
    protected void delete() {
        LoggerUtil.verbose(this, "Deleting...");
        this.getPlayers().forEach(gamePlayer -> {
            PlayerUtil.clean(gamePlayer, GameMode.ADVENTURE);
            gamePlayer.leaveGame(null);
        });
        gameState = GameState.DELETED;
    }

    @Override
    public Set<? extends GamePlayer> getPlayers() {
        return Set.copyOf(players);
    }

    /**
     * Utility method for developers to return specific type of GamePlayer
     * @param clazz the type to return
     * @param <P> the type which extends from GamePlayer
     * @return an immutable set of GamePlayer of specified type
     * @throws ClassCastException if the object is not assignable to the provided class
     */
    protected <P extends GamePlayer> Set<P> getPlayers(Class<P> clazz) {
        return this.getPlayers().stream()
                .map(clazz::cast)
                .collect(Collectors.toUnmodifiableSet());
    }

    /**
     * @param gamePlayer the player to add
     * @apiNote Use {@link GamePlayer#joinGame(Game)}
     */
    @ApiStatus.Internal
    public void addPlayer(GamePlayer gamePlayer) {
        if (!players.add(gamePlayer))
            throw new IllegalStateException(gamePlayer + " is already apart of this game");
    }

    /**
     * @param gamePlayer the player to remove
     * @apiNote Use {@link GamePlayer#leaveGame()}
     */
    @ApiStatus.Internal
    public void removePlayer(GamePlayer gamePlayer) {
        if (!players.remove(gamePlayer))
            throw new IllegalStateException(gamePlayer + " is not apart of this game");
    }

    /**
     * @return whether the minimum player requirements for this game has met
     */
    public boolean hasMinimumPlayers() {
        return this.getPlayers().size() >= settings.minPlayers;
    }

    /**
     * @return whether this game has begun
     */
    public boolean hasBegun() {
        return gameState != GameState.WAITING_FOR_PLAYERS && gameState != GameState.STARTING;
    }

    /**
     * @return whether this game is about to end, or has ended
     */
    public boolean hasEnded() {
        return gameState == GameState.ENDING || gameState == GameState.DELETED;
    }

    /**
     * @return whether this game is forced to start
     * @apiNote This method will always return <b>fasle</b> if this game has not yet started.
     */
    public boolean isForcedToStart() {
        return force;
    }

    /**
     * @return whether this game is full
     */
    public boolean isFull() {
        return this.getPlayers().size() >= settings.maxPlayers();
    }

    /**
     * @return whether this game is in a joinable state
     * @see #isFull()
     */
    public boolean isJoinable() {
        return Util.equalsAny(gameState, GameState.WAITING_FOR_PLAYERS, GameState.STARTING, GameState.INGAME);
    }

    /**
     * @return whether this game is about to start (game start countdown is ticking)
     */
    public boolean isAboutToStart() {
        return gameState == GameState.STARTING;
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
     * @return the prefix
     */
    public Optional<String> getPrefix() {
        return Optional.ofNullable(prefix);
    }

    public void setPrefix(@Nullable String prefix) {
        this.prefix = prefix;
    }

    /**
     * @return the game settings
     */
    public Settings getSettings() {
        return settings;
    }

    public UUID getUuid() {
        return uuid;
    }

    public String getDisplayUuid() {
        return uuid.toString().split("-")[0];
    }

    /**
     * @return whether this game is safe for settings modification.
     */
    public boolean isSafeForSettingsModification() {
        return Util.equalsAny(gameState, GameState.LOADING, GameState.WAITING_FOR_PLAYERS);
    }

    /**
     * @return whether this game is in setup state.
     */
    public boolean isSetup() {
        return gameState == GameState.LOADING;
    }

    /**
     * Removes games marked as {@link GameState#DELETED} from the registry.
     */
    protected static void garbageCollection() {
        // Prevent CCME
        Map<UUID, Game> games = new HashMap<>(GAMES);
        boolean clearCache = false;
        for (Game game : games.values()) {
            if (game.getGameState() == GameState.DELETED) {
                clearCache = true;
                GAMES.remove(game.getUuid());
            }
        }
        if (clearCache)
            GameCommand.UUIDS_CACHE = null;
    }

    @Override
    public String toString() {
        return "Game{" +
                "uuid=" + uuid +
                ", players=" + players +
                ", gameState=" + gameState +
                ", settings=" + settings +
                ", started=" + started +
                ", gameStartTask=" + gameStartTask +
                ", force=" + force +
                '}';
    }

}
