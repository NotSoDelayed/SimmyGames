package me.notsodelayed.simmygameapi.api.game;

import me.notsodelayed.simmygameapi.api.entity.GamePlayer;
import me.notsodelayed.simmygameapi.api.map.GameMap;
import me.notsodelayed.simmygameapi.api.map.MapChoice;
import me.notsodelayed.simmygameapi.api.team.GameTeam;
import me.notsodelayed.simmygameapi.api.team.GameTeamManager;
import org.jetbrains.annotations.NotNull;

/**
 * Represents a team game.
 * @param <T> any type which extends from {@link GameTeam}
 */
public abstract class TeamGame<T extends GameTeam, P extends GamePlayer> extends Game<P> {

    private GameTeamManager<T,P> teamManager;

    protected TeamGame(int minPlayers, int maxPlayers, @NotNull GameMap queriedGameMap) {
        super(minPlayers, maxPlayers, queriedGameMap);
        this.teamManager = new GameTeamManager<>(this);
    }

    protected TeamGame(int minPlayers, int maxPlayers, @NotNull MapChoice mapChoice) {
        super(minPlayers, maxPlayers, mapChoice);
        this.teamManager = new GameTeamManager<>(this);
    }

    @Override
    protected void delete() {
        super.delete();
        teamManager.unregisterAll();
    }

    /**
     * @return the team manager
     */
    public GameTeamManager<T,P> getTeamManager() {
        return teamManager;
    }

}
