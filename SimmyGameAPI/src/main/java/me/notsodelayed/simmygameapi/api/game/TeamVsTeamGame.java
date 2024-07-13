package me.notsodelayed.simmygameapi.api.game;

import me.notsodelayed.simmygameapi.api.entity.GamePlayer;
import me.notsodelayed.simmygameapi.api.map.GameMap;
import me.notsodelayed.simmygameapi.api.map.MapChoice;
import me.notsodelayed.simmygameapi.api.team.GameTeam;
import org.jetbrains.annotations.NotNull;

/**
 * Represents a team vs team game.
 */
public abstract class TeamVsTeamGame<T extends GameTeam, P extends GamePlayer> extends TeamGame<T, P> {

    private final T teamAlpha, teamBeta;

    protected TeamVsTeamGame(int minPlayers, int maxPlayers, @NotNull GameMap queriedGameMap, @NotNull T teamAlpha, @NotNull T teamBeta) {
        super(minPlayers, maxPlayers, queriedGameMap);
        this.teamAlpha = teamAlpha;
        this.teamBeta = teamBeta;
        getTeamManager().registerTeam(teamAlpha);
        getTeamManager().registerTeam(teamBeta);
    }

    protected TeamVsTeamGame(int minPlayers, int maxPlayers, @NotNull MapChoice mapChoice, @NotNull T teamAlpha, @NotNull T teamBeta) {
        super(minPlayers, maxPlayers, mapChoice);
        this.teamAlpha = teamAlpha;
        this.teamBeta = teamBeta;
        getTeamManager().registerTeam(teamAlpha);
        getTeamManager().registerTeam(teamBeta);
    }

    /**
     * @param team the team
     * @return the opposite team
     * @throws IllegalArgumentException if the provided team is neither {@link #getTeamAlpha()} nor {@link #getTeamBeta()}
     */
    public T getRivalTeam(T team) {
        if (team == teamAlpha)
            return teamBeta;
        if (team == teamBeta)
            return teamAlpha;
        throw new IllegalArgumentException("Called with unassociated team " + team);
    }

    /**
     * @return the team alpha
     */
    public T getTeamAlpha() {
        return teamAlpha;
    }

    /**
     * @return the team beta
     */
    public T getTeamBeta() {
        return teamBeta;
    }

}
