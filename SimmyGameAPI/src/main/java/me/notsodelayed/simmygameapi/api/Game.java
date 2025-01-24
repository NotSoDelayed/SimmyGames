package me.notsodelayed.simmygameapi.api;

import java.util.*;
import java.util.Map;
import java.util.concurrent.CompletableFuture;
import java.util.stream.Collectors;

import com.google.common.base.Preconditions;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import org.bukkit.GameMode;
import org.bukkit.Location;
import org.bukkit.Sound;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.ApiStatus;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import me.notsodelayed.simmygameapi.SimmyGameAPI;
import me.notsodelayed.simmygameapi.api.util.AscendingTimer;
import me.notsodelayed.simmygameapi.api.util.DescendingTimer;
import me.notsodelayed.simmygameapi.util.LoggerUtil;
import me.notsodelayed.simmygameapi.util.PlayerUtil;
import me.notsodelayed.simmygameapi.util.StringUtil;

/**
 * Represents a game.
 */
// TODO method for setup tasks
public abstract class Game implements BaseGame {

    private static final Map<UUID, Game> GAMES = new HashMap<>();
    private final UUID uuid = UUID.randomUUID();
    private final Set<GamePlayer> players = new HashSet<>();
    private GameState gameState = GameState.LOADING;
    private final GameSettings settings;
    private final DescendingTimer countdown = new DescendingTimer();
    private final AscendingTimer ingameTimer = new AscendingTimer();
    private final long created = System.currentTimeMillis();

    /**
     * @param minPlayers the minimum player count
     * @param maxPlayers the maximum player count
     * @apiNote This returns a Game instance with state {@link GameState#LOADING}, where it is not joinable.
     * @implSpec For custom game setup requirements, developers must override {@link #ready()} and call super before custom implementations.
     */
    protected Game(int minPlayers, int maxPlayers) {
        this.settings = new GameSettings(this)
                .maxPlayers(maxPlayers)
                .minPlayers(minPlayers);
        countdown.executeAt(seconds -> seconds == settings.startIn() || seconds % 10 == 0 || (seconds > 0 && seconds <= 5), seconds -> {
            dispatchMessage("<yellow>Game will start in " + seconds + "...");
            dispatchSound(Sound.BLOCK_NOTE_BLOCK_HAT, 2, 1);
        }).executeAt(seconds -> seconds == 0, seconds -> {
            gameState = GameState.INGAME;
            dispatchMessage("<green>Game has started!");
            init();
            ingameTimer.start();
        });
        GAMES.put(uuid, this);
    }

    /**
     * @return an immutable copy of registered active games
     */
    public static Map<UUID, Game> getGames() {
        garbageCollection();
        return Map.copyOf(GAMES);
    }

    public static @Nullable Game getGame(UUID uuid) {
        return GAMES.get(uuid);
    }

    /**
     * @param stringUuid the string representation or the first portion of a UUID
     * @return the matched game, otherwise null
     */
    public static @Nullable Game getGame(String stringUuid) {
        return GAMES.values().stream()
                .filter(game -> {
                    if (stringUuid.length() == 8)
                        return game.getUuid().toString().split("-")[0].equals(stringUuid);
                    if (stringUuid.length() == 36)
                        return game.getUuid().toString().equals(stringUuid);
                    return false;
                }).findAny().orElse(null);
    }

    @Override
    public void ready() throws IllegalStateException {
        BaseGame.super.ready();
        gameState = GameState.WAITING_FOR_PLAYERS;
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
        if (!force && !hasMetGameRequirements()) {
            LoggerUtil.verbose(this, "Game start countdown aborted due to game requirements not met.");
            return;
        }
        gameState = GameState.STARTING;
        countdown.start(startIn);
    }

    /**
     * Initialise the game.
     * @implNote Get {@link CompletableFuture super.init()}, implement in {@link CompletableFuture#thenRun(Runnable)}, and return it.
     */
    protected abstract void init();

    @Override
    public void end() {
        gameState = GameState.ENDING;
        ingameTimer.end();
        ((DescendingTimer) new DescendingTimer()
                .executeAt(seconds -> seconds == settings.endIn() || seconds % 10 == 0 || seconds <= 5, seconds -> dispatchPrefixedMessage("<yellow>Game ending in " + seconds + "...")))
                .executeAtEnd(seconds -> delete())
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
            PlayerUtil.reset(gamePlayer, GameMode.ADVENTURE);
            gamePlayer.leaveGame();
        });
        gameState = GameState.DELETED;
        GAMES.remove(this.getUuid());
    }

    public void dispatchMessage(Component message) {
        for (Player player : getBukkitPlayers())
            player.sendMessage(message);
    }

    /**
     * @param message the message (supported by MiniMessage)
     */
    public void dispatchMessage(String message) {
        for (Player player : getBukkitPlayers())
            player.sendMessage(SimmyGameAPI.miniMessage().deserialize(message));
    }

    public void dispatchPrefixedMessage(@NotNull Component message) {
        dispatchMessage(getPrefix().append(Component.text(" ", NamedTextColor.WHITE).append(message)));
    }

    /**
     * @param message the message (supported by MiniMessage)
     */
    public void dispatchPrefixedMessage(@NotNull String message) {
        dispatchPrefixedMessage(SimmyGameAPI.miniMessage().deserialize(message));
    }

    /**
     * @param location the location for the sound to be played at
     * @param sound the sound
     * @param volume the volume
     * @param pitch the pitch
     */
    public void dispatchSound(Location location, Sound sound, float volume, float pitch) {
        for (Player player : getBukkitPlayers()) {
            Location loc = location != null ? location : player.getLocation();
            player.playSound(loc, sound, volume, pitch);
        }
    }

    public void dispatchSound(Sound sound, float volume, float pitch) {
        dispatchSound(null, sound, volume, pitch);
    }

    @Override
    public void showInfo(CommandSender sender) {
        Component info = Component.newline()
                .append(Component.text(this.getFormattedName()))
                .appendSpace()
                .append(Component.text(StringUtil.smallText(gameState.toString()), gameState.getColor()))
                .appendNewline()
                .append(Component.text("Players: "))
                .append(Component.text(playing(), NamedTextColor.GREEN))
                .append(Component.text("/", NamedTextColor.GRAY))
                .append(Component.text(settings.maxPlayers()));
        sender.sendMessage(info);
    }

    @Override
    public UUID getUuid() {
        return uuid;
    }

    public int playing() {
        return players.size();
    }

    /**
     * @see GamePlayer#GamePlayer(Player, Game)
     */
    @ApiStatus.Internal
    void addPlayer(GamePlayer gamePlayer) {
        if (!players.add(gamePlayer))
            throw new IllegalStateException(gamePlayer + " is already apart of this game");

        // TODO add more waiting lobby stuff
        PlayerUtil.reset(gamePlayer, GameMode.ADVENTURE);

        dispatchPrefixedMessage(String.format("<yellow>%s has joined! (%s/%s)", gamePlayer.getName(), players.size(), settings.maxPlayers()));
        if (settings.startWithMinimumPlayers() && hasMetGameRequirements())
            start();
    }

    /**
     * @param gamePlayer the player to remove
     * @apiNote Use {@link GamePlayer#leaveGame()}
     */
    @ApiStatus.Internal
    void removePlayer(GamePlayer gamePlayer) {
        if (!players.remove(gamePlayer))
            throw new IllegalStateException(gamePlayer + " is not apart of this game");
        dispatchPrefixedMessage(String.format("<yellow>%s has left! (%s/%s)", gamePlayer.getName(), players.size(), settings.maxPlayers()));
        if (isAboutToStart() && !hasMetGameRequirements())
            countdown.cancel();
    }

    /**
     * @return an immutable set of game players
     * @implNote Developers may override this with {@link #getPlayers(Class)} to explicitly return a specific type of game players.
     */
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
        return players.stream()
                .map(clazz::cast)
                .collect(Collectors.toUnmodifiableSet());
    }

    /**
     * @return a formatted name of this game (CLASS_NAME-PORTION_1_UUID)
     */
    public String getFormattedName() {
        return getClass().getSimpleName() + "-" + StringUtil.getDisplayUuid(uuid);
    }

    @Override
    public GameState getGameState() {
        return gameState;
    }

    protected void setGameState(@NotNull GameState gameState) {
        this.gameState = gameState;
    }

    @Override
    public GameSettings getSettings() {
        return settings;
    }

    /**
     * @return the game start countdown of this game
     */
    public DescendingTimer getCountdown() {
        return countdown;
    }

    public AscendingTimer getIngameTimer() {
        return ingameTimer;
    }

    /**
     * @return the time in millis from epoch, of this game's creation
     */
    public long createdAt() {
        return created;
    }

    public abstract @NotNull GameMode getGameMode();

    public abstract @NotNull Component getPrefix();

    /**
     * @return whether the minimum player requirements for this game has met
     */
    public boolean hasMinimumPlayers() {
        return this.getPlayers().size() >= settings.minPlayers();
    }

    /**
     * @return whether this game has met game requirements to start
     * @implNote Developers may override this for custom requirements.
     */
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
     * @return whether this game is joinable, such that <b>{@link GameState#isJoinableState()} == true</b> and <b>{@link Game#isFull()} == false</b>
     * @see #isFull()
     */
    public boolean isJoinable() {
        return gameState.isJoinableState() && !isFull();
    }

    /**
     * @return whether this game is about to start (game start countdown is ticking)
     */
    public boolean isAboutToStart() {
        return gameState == GameState.STARTING && countdown.isActive();
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
        for (Game game : games.values()) {
            if (game.getGameState() == GameState.DELETED)
                GAMES.remove(game.getUuid());
        }
    }

}
