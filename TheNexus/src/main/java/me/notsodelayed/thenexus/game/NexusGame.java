package me.notsodelayed.thenexus.game;

import java.util.Set;

import com.google.common.base.Preconditions;
import org.jetbrains.annotations.NotNull;

import me.notsodelayed.simmygameapi.api.game.GameState;
import me.notsodelayed.simmygameapi.api.game.MapGame;
import me.notsodelayed.simmygameapi.api.game.TeamVsTeamGame;
import me.notsodelayed.simmygameapi.api.game.map.GameMap;
import me.notsodelayed.simmygameapi.api.game.team.GameTeamManager;
import me.notsodelayed.thenexus.entity.NexusPlayer;
import me.notsodelayed.thenexus.entity.team.NexusTeam;
import me.notsodelayed.thenexus.map.NexusMap;

public abstract class NexusGame<M extends NexusMap, T extends NexusTeam> extends MapGame<M> implements TeamVsTeamGame<T> {

    private final GameTeamManager<T> teamManager;
    private T teamAlpha, teamBeta;

    /**
     * Creates a MapGame without pre-defined {@link GameMap}.
     *
     * @param minPlayers the minimum player count
     * @param maxPlayers the maximum player count
     * @apiNote This returns a MapGame instance with state {@link GameState#LOADING}, where it is not joinable. <p>Developers must call {@link #ready()} in post-setup. </p>
     * @implNote Developers must ensure <b>{@link #getMap()} != null</b> before {@link #tick()} is called.
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

    @Override
    public T getTeamAlpha() {
        return teamAlpha;
    }

    @Override
    public void setTeamAlpha(@NotNull T teamAlpha) {
        Preconditions.checkState(isSetupMode(), "game is not in setup state");
        this.teamAlpha = teamAlpha;
    }

    @Override
    public T getTeamBeta() {
        return teamBeta;
    }

    @Override
    public void setTeamBeta(@NotNull T teamBeta) {
        Preconditions.checkState(isSetupMode(), "game is not in setup state");
        this.teamBeta = teamBeta;
    }

}
