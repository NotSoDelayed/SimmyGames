package me.notsodelayed.simmygameapi.api.team;

import java.util.HashMap;
import java.util.Map;

import me.notsodelayed.simmygameapi.api.entity.GamePlayer;
import me.notsodelayed.simmygameapi.api.game.Game;
import me.notsodelayed.simmygameapi.api.game.TeamGame;
import org.jetbrains.annotations.ApiStatus;
import org.jetbrains.annotations.NotNull;

/**
 * Represents a {@link GameTeam} manager of a {@link Game}.
 */
public class GameTeamManager<T extends GameTeam, P extends GamePlayer> {

    private final Map<String, T> teams;

    public GameTeamManager(TeamGame<T,P> teamGame) {
        if (teamGame.getTeamManager() != null)
            throw new RuntimeException("TeamGame " + teamGame + " already has a GameTeamManager");
        teams = new HashMap<>();
    }

    /**
     * Registers a team into this manager.
     * @param gameTeam the team
     */
    @ApiStatus.Internal
    public void registerTeam(@NotNull T gameTeam) {
        teams.put(gameTeam.getId(), gameTeam);
    }

    /**
     * @return an immutable copy of registered teams
     */
    public Map<String, T> getTeams() {
        return Map.copyOf(teams);
    }

    /**
     * Unregisters all teams from this manager. Usually used during game deletion.
     */
    @ApiStatus.Internal
    public void unregisterAll() {
        teams.values().forEach(gameTeam -> gameTeam.getBukkitTeam().unregister());
    }

}
