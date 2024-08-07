package me.notsodelayed.simmygameapi.api.game;

import com.google.common.base.Preconditions;
import me.notsodelayed.simmygameapi.api.game.map.GameMap;

public abstract class MapGame<M extends GameMap> extends Game {

    private M map, queriedMap;
    protected boolean lockMap = false;

    /**
     * @param minPlayers the minimum player count
     * @param maxPlayers the maximum player count
     * @apiNote This returns a Game instance with state {@link GameState#LOADING}, where it is not joinable.
     * @implSpec For custom game setup requirements, developers must override {@link #ready()} and call super before custom implementations.
     */
    protected MapGame(int minPlayers, int maxPlayers) {
        super(minPlayers, maxPlayers);
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
     * @throws IllegalStateException if the map is locked from changing
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
     * @throws IllegalStateException if the map is locked from changing
     */
    protected void checkMapLock() {
        Preconditions.checkState(!lockMap, "map is locked from changing");
    }

}
