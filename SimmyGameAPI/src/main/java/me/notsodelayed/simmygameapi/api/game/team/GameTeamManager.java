package me.notsodelayed.simmygameapi.api.game.team;

import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.jetbrains.annotations.ApiStatus;
import org.jetbrains.annotations.NotNull;

import me.notsodelayed.simmygameapi.api.game.Game;
import me.notsodelayed.simmygameapi.util.Util;

/**
 * Represents a {@link GameTeam} manager of a {@link Game}.
 */
public class GameTeamManager<T extends GameTeam> {

    private final Map<String, T> teams;

    public GameTeamManager() {
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
     * @return a team with the smallest players size
     * @apiNote In case of multiple teams with the same smallest value is available, a random team from it will be picked.
     */
    public T getSmallestTeam() {
        if (teams.isEmpty())
            throw new IllegalStateException("no teams to compute");
        if (teams.size() == 1)
            return teams.values().iterator().next();
        List<T> sortedTeams = teams.values().stream().
                sorted(Comparator.comparingInt(team -> team.getPlayers().size()))
                .toList();
        int index = 0;
        int smallest = -1;
        for (T team : sortedTeams) {
            if (smallest == -1) {
                smallest = team.getPlayers().size();
                continue;
            }
            if (team.getPlayers().size() != smallest)
                break;
            index++;
        }
        if (index > 0)
            return sortedTeams.get(Util.getRandomInt(index));
        return sortedTeams.get(index);
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
