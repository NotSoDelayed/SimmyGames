package me.notsodelayed.simmygameapi.api.team;

import java.util.*;

import com.google.common.base.Preconditions;
import org.bukkit.Bukkit;
import org.bukkit.scoreboard.Scoreboard;
import org.bukkit.scoreboard.Team;
import org.jetbrains.annotations.ApiStatus;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import me.notsodelayed.simmygameapi.SimmyGameAPI;
import me.notsodelayed.simmygameapi.api.Game;
import me.notsodelayed.simmygameapi.api.GamePlayer;
import me.notsodelayed.simmygameapi.api.player.TeamPlayer;
import me.notsodelayed.simmygameapi.util.Util;

/**
 * Represents a {@link GameTeam} manager of a {@link Game}.
 */
public class GameTeamManager<T extends GameTeam> {

    private final Map<String, T> teams = new HashMap<>();
    private final Map<T, Team> teamsPair = new HashMap<>();
    private final Scoreboard scoreboard;

    public GameTeamManager() {
        scoreboard = Bukkit.getScoreboardManager().getNewScoreboard();
    }

    /**
     * Registers a team into this manager.
     * @param team the team
     */
    @ApiStatus.Internal
    public void registerTeam(@NotNull T team) {
        Preconditions.checkState(!teams.containsValue(team), "team '" + team.getId() + "' already registered");
        Team bukkitTeam = scoreboard.registerNewTeam(team.getId());
        bukkitTeam.color(team.getColor());
        bukkitTeam.displayName(team.getDisplayName());
        teams.put(team.getId(), team);
        teamsPair.put(team, bukkitTeam);
    }

    /**
     * Assigns a player to a random team (prioritising smaller teams). Nothing will happen if the player is already in a team.
     * @param player the player
     * @return the chosen team
     * @throws IllegalStateException if there are no teams registered
     */
    public T joinRandom(TeamPlayer<T> player) { // TODO account for team size
        Preconditions.checkState(!teams.isEmpty(), "no teams available");
        if (player.getTeam() != null)
            return player.getTeam();
        List<T> teams = this.teams.values().stream().sorted().toList();
        // Prioritize the lowest team
        T team = Util.getRandomInt(2) != 2 ? teams.getFirst() : teams.get(Util.getRandomInt(teams.size()));
        joinTeam(team, (GamePlayer) player);
        return team;
    }

    /**
     * @param team the team
     * @throws IllegalArgumentException if the provided team is not registered in this manager
     * @throws IllegalStateException if the provided player is already assigned to a team
     */
    public void joinTeam(T team, GamePlayer player) {
        Preconditions.checkArgument(teamsPair.containsKey(team), "team is not registered in the manager");
        team.addPlayer(player);
        player.message("You have joined team " + SimmyGameAPI.miniMessage().serialize(team.getDisplayName()) + "<reset>!");
    }

    public @Nullable T getTeam(GamePlayer player) {
        Map<String,Integer> map = new HashMap<>();
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
    public Collection<T> getTeams() {
        return Collections.unmodifiableCollection(teams.values());
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
