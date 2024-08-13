package me.notsodelayed.thenexus.game;

import me.notsodelayed.simmygameapi.api.game.GameState;
import me.notsodelayed.simmygameapi.api.game.map.GameMap;

public class TntNexusGame extends NexusGame {

    /**
     * Creates a MapGame without pre-defined {@link GameMap}.
     *
     * @param minPlayers the minimum player count
     * @param maxPlayers the maximum player count
     * @apiNote This returns a MapGame instance with state {@link GameState#LOADING}, where it is not joinable. <p>Developers must call {@link #ready()} in post-setup. </p>
     * @implNote Developers must ensure <b>{@link #getMap()} != null</b> before {@link #tick()} is called.
     */
    protected TntNexusGame(int minPlayers, int maxPlayers) {
        super(minPlayers, maxPlayers);
    }

    @Override
    protected boolean init() {
        return true;
    }

    @Override
    public void tick() {
        super.tick();
        // TODO initiate game timers, virtual chests, triple nexus mechanics etc
    }

}
