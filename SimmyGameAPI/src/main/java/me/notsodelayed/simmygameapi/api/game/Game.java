package me.notsodelayed.simmygameapi.api.game;

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
import org.bukkit.Bukkit;
import org.bukkit.GameMode;
import org.bukkit.Sound;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitRunnable;
import org.jetbrains.annotations.ApiStatus;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import me.notsodelayed.simmygameapi.SimmyGameAPI;
import me.notsodelayed.simmygameapi.api.game.player.GamePlayer;
import me.notsodelayed.simmygameapi.api.util.Countdown;
import me.notsodelayed.simmygameapi.command.GameCommand;
import me.notsodelayed.simmygameapi.util.CompareUtil;
import me.notsodelayed.simmygameapi.util.LoggerUtil;
import me.notsodelayed.simmygameapi.util.PlayerUtil;
import me.notsodelayed.simmygameapi.util.StringUtil;

/**
 * Represents a game.
 */
public abstract class Game implements BaseGame {

    private static final Map<UUID, Game> GAMES = new HashMap<>();
    @Nullable
    private String prefix;
    private final UUID uuid;
    private final GameSettings settings;
    private GameState gameState = GameState.LOADING;
    private final Countdown countdown;
    private final Set<GamePlayer> players;

    /**
     * @param minPlayers the minimum player count
     * @param maxPlayers the maximum player count
     * @apiNote This returns a Game instance with state {@link GameState#LOADING}, where it is not joinable.
     * @implSpec For custom game setup requirements, developers must override {@link #ready()} and call super before custom implementations.
     */
    protected Game(int minPlayers, int maxPlayers) {
        this.uuid = UUID.randomUUID();
        this.players = new HashSet<>();
        this.settings = new GameSettings(this)
                .maxPlayers(maxPlayers)
                .minPlayers(minPlayers);
        this.countdown = new Countdown(this);
        GAMES.put(uuid, this);
        GameCommand.UUIDS_CACHE = null;
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
     * @return whether this game's initialisation is successful
     * @apiNote Called after {@link #hasMetGameRequirements()}. In event of unhandled exception occurred in this method, it will return false.
     * @implNote Developers may implement custom initialisation tasks. Ensure this method returns <b>true</b> (preferably, return super, unless for special reasons) to allow the game start sequence to proceed.
     */
    protected boolean init() {
        if (countdown.tasksCount() == 0)
            countdown.executeAt(seconds -> seconds == settings.startIn() || seconds % 10 == 0 || seconds <= 5, (seconds, game) -> {
                dispatchMessage("&eGame will start in " + seconds + "...");
                dispatchSound(Sound.NOTE_STICKS, 1);
            }).executeAfterDepletes((seconds, game) -> {
                gameState = GameState.INGAME;
                dispatchMessage("&aGame has started!");
                tick();
            });
        return true;
    }

    public boolean hasMetGameRequirements() {
        return hasMinimumPlayers();
    }

    @Override
    public void start() {
        start(false);
    }

    public void start(boolean force) {
        start(settings.startIn(), force);
    }

    public void start(int startIn, boolean force) {
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
            ex.printStackTrace(System.err);
            return;
        }
        gameState = GameState.STARTING;
        countdown.start(startIn);
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
        GAMES.remove(this.getUuid());
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
        return this.getPlayers().size() >= settings.minPlayers();
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
        return CompareUtil.equalsAny(gameState, GameState.WAITING_FOR_PLAYERS, GameState.STARTING, GameState.INGAME);
    }

    /**
     * @return whether this game is about to start (game start countdown is ticking)
     */
    public boolean isAboutToStart() {
        return gameState == GameState.STARTING;
    }

    @Override
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

    public Game setPrefix(@Nullable String prefix) {
        this.prefix = prefix;
        return this;
    }

    @Override
    public UUID getUuid() {
        return uuid;
    }

    @Override
    public GameSettings getSettings() {
        return settings;
    }

    /**
     * @return whether this game is safe for settings modification.
     */
    public boolean isSafeForSettingsModifications() {
        return CompareUtil.equalsAny(gameState, GameState.LOADING, GameState.WAITING_FOR_PLAYERS);
    }

    /**
     * @return the countdown manager of this game
     */
    public Countdown getCountdown() {
        return countdown;
    }

    /**
     * @return whether this game is in setup state.
     */
    public boolean isSetupMode() {
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

}
