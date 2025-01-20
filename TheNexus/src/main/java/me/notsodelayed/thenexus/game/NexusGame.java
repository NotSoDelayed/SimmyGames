package me.notsodelayed.thenexus.game;

import java.util.Set;

import me.notsodelayed.simmygameapi.api.game.GameState;
import me.notsodelayed.simmygameapi.api.game.MapGame;
import me.notsodelayed.simmygameapi.api.game.TeamVsTeamGame;
import me.notsodelayed.simmygameapi.api.map.GameMap;
import me.notsodelayed.simmygameapi.api.team.GameTeamManager;
import me.notsodelayed.thenexus.team.NexusTeam;
import me.notsodelayed.thenexus.map.NexusMap;

public abstract class NexusGame<M extends NexusMap, T extends NexusTeam> extends MapGame<M> implements TeamVsTeamGame<T> {

    private final GameTeamManager<T> teamManager;

    /**
     * Creates a MapGame without pre-defined {@link GameMap}.
     *
     * @param minPlayers the minimum player count
     * @param maxPlayers the maximum player count
     * @apiNote This returns a MapGame instance with state {@link GameState#LOADING}, where it is not joinable. <p>Developers must call {@link #ready()} in post-setup. </p>
     * @implNote Developers must ensure <b>{@link #getMap()} != null</b> before {@link #init()} is called.
     */
    protected NexusGame(int minPlayers, int maxPlayers) {
        super(minPlayers, maxPlayers);
        teamManager = new GameTeamManager<>();
    }

    @Override
    public Set<NexusPlayer> getPlayers() {
        return getPlayers(NexusPlayer.class);
    }

    @Override
    public GameTeamManager<T> getTeamManager() {
        return teamManager;
    }

}
