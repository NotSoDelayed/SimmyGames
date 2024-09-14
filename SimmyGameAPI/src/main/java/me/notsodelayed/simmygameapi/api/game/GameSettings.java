package me.notsodelayed.simmygameapi.api.game;

import com.google.common.base.Preconditions;

/**
 * Represents a {@link Game} settings.
 *
 * @apiNote Usage of setters are only allowed while <b>{@link Game#hasBegun()} = true</b>
 */
public class GameSettings {

    private final Game game;
    private int endIn = 20;
    private int startIn = 30;
    private int minPlayers, maxPlayers;
    private boolean startWithMinimumPlayers = true;

    GameSettings(Game game) {
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
    public GameSettings endIn(int endIn) {
        Preconditions.checkState(game.isSafeForSettingsModifications(), "game is not in idle states");
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
     * @param minPlayers the game minimum players, where minPlayers > 0
     * @return this instance, for chaining
     */
    public GameSettings minPlayers(int minPlayers) {
        Preconditions.checkState(game.isSafeForSettingsModifications(), "game is not in idle states");
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
        Preconditions.checkState(game.isSafeForSettingsModifications(), "game is not in idle states");
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
        Preconditions.checkState(game.isSafeForSettingsModifications(), "game is not in idle states");
        this.startIn = Math.max(startIn, 5);
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
