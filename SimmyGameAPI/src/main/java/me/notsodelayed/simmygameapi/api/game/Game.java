package me.notsodelayed.simmygameapi.api.game;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.UUID;
import java.util.stream.Collectors;

import com.google.common.base.Preconditions;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import org.bukkit.GameMode;
import org.bukkit.Sound;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.ApiStatus;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import me.notsodelayed.simmygameapi.api.game.player.GamePlayer;
import me.notsodelayed.simmygameapi.api.util.AscendingTimer;
import me.notsodelayed.simmygameapi.api.util.DescendingTimer;
import me.notsodelayed.simmygameapi.api.util.Timer;
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
    private Component prefix;
    private final UUID uuid;
    private final GameSettings settings;
    private GameState gameState = GameState.LOADING;
    private final DescendingTimer countdown;
    private final Set<GamePlayer> players;
    private boolean setupCountdown = true;

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
        this.countdown = new DescendingTimer();
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

    @Override
    public void ready() throws IllegalStateException {
        BaseGame.super.ready();
        gameState = GameState.WAITING_FOR_PLAYERS;
    }

    /**
     * @return whether this game's initialisation is successful
     * @apiNote Called after {@link #hasMetGameRequirements()}. In event of unhandled exception occurred in this method, it will return false.
     * @implNote Developers may implement custom initialisation tasks. Ensure this method returns <b>true</b> (preferably, return super, unless for special reasons) to allow the game start sequence to proceed.
     */
    protected boolean init() {
        if (setupCountdown) {
            countdown.executeAt(seconds -> seconds == settings.startIn() || seconds % 10 == 0 || seconds <= 5, seconds -> {
                dispatchMessage("&eGame will start in " + seconds + "...");
                dispatchSound(Sound.BLOCK_NOTE_BLOCK_HAT, 1);
            }).executeAt(seconds -> seconds == 0, seconds -> {
                gameState = GameState.INGAME;
                dispatchMessage("&aGame has started!");
                tick();
            });
            setupCountdown = false;
        }
        return true;
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

    @Override
    public void end() {
        ((DescendingTimer) new DescendingTimer()
                .executeAt(seconds -> seconds == settings.endIn() || seconds % 10 == 0 || seconds <= 5, seconds -> delete()))
                .start(settings.endIn());
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
            gamePlayer.leaveGame();
        });
        gameState = GameState.DELETED;
        GAMES.remove(this.getUuid());
    }

    public void dispatchMessage(String message) {
        for (Player player : getBukkitPlayers())
            player.sendMessage(StringUtil.color(message));
    }

    public void dispatchMessage(Component message) {
        for (Player player : getBukkitPlayers())
            player.sendMessage(message);
    }

    public void dispatchPrefixedMessage(@NotNull String message) {
        dispatchMessage(prefix.append(Component.text(" ").color(NamedTextColor.WHITE)).append(StringUtil.colorToComponent(message)));
    }

    public void dispatchPrefixedMessage(@NotNull Component message) {
        dispatchMessage(prefix.append(Component.text(" ").color(NamedTextColor.WHITE)).append(message));
    }

    public void dispatchSound(Sound sound, float pitch) {
        dispatchSound(sound, 2, pitch);
    }

    public void dispatchSound(Sound sound, float volume, float pitch) {
        for (Player player : getBukkitPlayers()) {
            player.playSound(player.getLocation(), sound, volume, pitch);
        }
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
     * @see GamePlayer#GamePlayer(Player, Game)
     */
    @ApiStatus.Internal
    public void addPlayer(GamePlayer gamePlayer) {
        if (!players.add(gamePlayer))
            throw new IllegalStateException(gamePlayer + " is already apart of this game");
        dispatchPrefixedMessage(String.format("&e%s has joined! (%s/%s)", gamePlayer.getName(), players.size(), settings.maxPlayers()));
        if (settings.startWithMinimumPlayers() && hasMetGameRequirements())
            start();
    }

    /**
     * @param gamePlayer the player to remove
     * @apiNote Use {@link GamePlayer#leaveGame()}
     */
    @ApiStatus.Internal
    public void removePlayer(GamePlayer gamePlayer) {
        if (!players.remove(gamePlayer))
            throw new IllegalStateException(gamePlayer + " is not apart of this game");
        if (isAboutToStart() && !hasMetGameRequirements())
            countdown.cancel();
    }

    /**
     * @return whether the minimum player requirements for this game has met
     */
    public boolean hasMinimumPlayers() {
        return this.getPlayers().size() >= settings.minPlayers();
    }

    public boolean hasMetGameRequirements() {
        return hasMinimumPlayers();
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

    protected void setGameState(@NotNull GameState gameState) {
        this.gameState = gameState;
    }

    public Component getPrefix() {
        return prefix;
    }

    public Game setPrefix(@Nullable String prefix) {
        this.prefix = prefix != null ? StringUtil.colorToComponent(prefix) : Component.empty();
        return this;
    }

    public Game setPrefix(@NotNull Component prefix) {
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
     * @return the game start countdown of this game
     */
    public Timer getCountdown() {
        return countdown;
    }

    public abstract @NotNull GameMode getGameMode();

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
