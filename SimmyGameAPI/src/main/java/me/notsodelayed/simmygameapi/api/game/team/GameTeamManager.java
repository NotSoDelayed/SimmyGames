package me.notsodelayed.simmygameapi.api.game.team;

import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import com.google.common.base.Preconditions;
import org.bukkit.Bukkit;
import org.bukkit.scoreboard.Scoreboard;
import org.bukkit.scoreboard.Team;
import org.jetbrains.annotations.ApiStatus;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import me.notsodelayed.simmygameapi.api.game.Game;
import me.notsodelayed.simmygameapi.api.game.player.TeamPlayer;
import me.notsodelayed.simmygameapi.util.Util;

/**
 * Represents a {@link GameTeam} manager of a {@link Game}.
 */
public class GameTeamManager<T extends GameTeam> {

    private final Map<String, T> teams;
    private final Map<T, Team> teamsPair;
    private final Scoreboard scoreboard;

    public GameTeamManager() {
        teams = new HashMap<>();
        teamsPair = new HashMap<>();
        scoreboard = Bukkit.getScoreboardManager().getNewScoreboard();
    }

    /**
     * Registers a team into this manager.
     * @param team the team
     */
    @ApiStatus.Internal
    public void registerTeam(@NotNull T team) {
        Team bukkitTeam = scoreboard.registerNewTeam(team.getId());
        bukkitTeam.color(team.getColor());
        bukkitTeam.displayName(team.getDisplayName());
        teams.put(team.getId(), team);
        teamsPair.put(team, bukkitTeam);
    }

    /**
     * @param team the team
     * @throws IllegalArgumentException if the provided team is not registered in this manager
     * @throws IllegalStateException if the provided player is already assigned to a team
     */
    public void joinTeam(T team, TeamPlayer<T> player) {
        Preconditions.checkState(player.getTeam() == null, "player is already assigned to a team");
        team.addPlayer(player);
    }

    public @Nullable T getTeam(TeamPlayer<?> player) {
        Optional<T> qTeam = teams.values().stream()
                .filter(team -> team.getPlayers().contains(player))
                .findFirst();
        return qTeam.orElse(null);
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
        teamsPair.values().forEach(Team::unregister);
    }

    /**
     * @return the scoreboard for housing teams
     */
    public Scoreboard getScoreboard() {
        return scoreboard;
    }

}
