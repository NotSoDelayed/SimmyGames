package me.notsodelayed.simmygameapi.api;

import java.util.HashSet;
import java.util.Set;

import me.notsodelayed.simmygameapi.util.CompareUtil;

/**
 * Represents a {@link Game} settings.
 *
 * @apiNote Usage of setters are only allowed while <b>{@link Game#isSetupMode()} = true</b>
 */
public class GameSettings {

    private final Game game;
    private int endIn = 20;
    // TODO startIn reset
    private int startIn = 10;
    private int minPlayers, maxPlayers;
    private boolean startWithMinimumPlayers = true;
    private final Set<GameState> joinableStates = new HashSet<>();

    GameSettings(Game game) {
        this.game = game;
        joinableStates.add(GameState.WAITING_FOR_PLAYERS);
        joinableStates.add(GameState.STARTING);
        joinableStates.add(GameState.INGAME);
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
    public GameSettings endIn(int endIn) {
        checkSafeForSettingsModifications();
        this.endIn = endIn;
        return this;
    }

    /**
     * @return the game states where players can join.
     * <p>
     * Default values: {@link GameState#WAITING_FOR_PLAYERS}, {@link GameState#STARTING}, {@link GameState#INGAME}
     * @see #allowJoinDuring(GameState)
     * @see #disallowJoinDuring(GameState)
     */
    public Set<GameState> joinableStates() {
        return Set.copyOf(joinableStates);
    }

    /**
     * @param gameState the game state to allow player joins
     * @return this instance, for chaining
     * @see #disallowJoinDuring(GameState)
     * @see #joinableStates()
     */
    public GameSettings allowJoinDuring(GameState gameState) {
        checkSafeForSettingsModifications();
        joinableStates.add(gameState);
        return this;
    }

    /**
     * @param gameState the game state to disallow player joins
     * @return this instance, for chaining
     * @see #allowJoinDuring(GameState)
     * @see #joinableStates()
     */
    public GameSettings disallowJoinDuring(GameState gameState) {
        checkSafeForSettingsModifications();
        joinableStates.remove(gameState);
        return this;
    }

    /**
     * @return the game minimum players
     */
    public int minPlayers() {
        return minPlayers;
    }

    /**
     * @param minPlayers the game minimum players, where minPlayers > 0
     * @return this instance, for chaining
     */
    public GameSettings minPlayers(int minPlayers) {
        checkSafeForSettingsModifications();
        this.minPlayers = Math.min(1, minPlayers);
        return this;
    }

    /**
     * @return the game maximum players
     */
    public int maxPlayers() {
        return maxPlayers;
    }

    /**
     * @param maxPlayers the game maximum players, where maxPlayers > 0
     * @return this instance, for chaining
     */
    public GameSettings maxPlayers(int maxPlayers) {
        checkSafeForSettingsModifications();
        this.maxPlayers = Math.max(1, maxPlayers);
        return this;
    }

    /**
     * @return the game start warmup, in seconds, where seconds >= 5
     */
    public int startIn() {
        return startIn;
    }

    /**
     * @param startIn the game start warmup, in seconds, where seconds >= 5
     * @return this instance, for chaining
     */
    public GameSettings startIn(int startIn) {
        checkSafeForSettingsModifications();
        this.startIn = Math.max(startIn, 6);
        return this;
    }

    /**
     * @return whether this game should start automatically whenever {@link Game#hasMinimumPlayers()} = true
     */
    public boolean startWithMinimumPlayers() {
        return startWithMinimumPlayers;
    }

    /**
     * @param startWithMinimumPlayers whether this game should start automatically whenever {@link Game#hasMinimumPlayers()} = true
     * @return this instance, for chaining
     */
    public GameSettings startWithMinimumPlayers(boolean startWithMinimumPlayers) {
        this.startWithMinimumPlayers = startWithMinimumPlayers;
        return this;
    }

    protected void checkSafeForSettingsModifications() {
        if (!CompareUtil.equalsAny(game.getGameState(), GameState.LOADING, GameState.WAITING_FOR_PLAYERS))
            throw new IllegalStateException("game is not safe for settings modifications");
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
