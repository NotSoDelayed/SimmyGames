package me.notsodelayed.simmygameapi.api.team;

import java.util.*;

import com.google.common.base.Preconditions;
import net.kyori.adventure.text.format.NamedTextColor;
import org.bukkit.Bukkit;
import org.bukkit.scoreboard.Scoreboard;
import org.bukkit.scoreboard.Team;
import org.jetbrains.annotations.ApiStatus;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import me.notsodelayed.simmygameapi.SimmyGameAPI;
import me.notsodelayed.simmygameapi.api.Game;
import me.notsodelayed.simmygameapi.api.GamePlayer;
import me.notsodelayed.simmygameapi.api.GameTeam;
import me.notsodelayed.simmygameapi.util.Util;

/**
 * Represents a {@link GameTeam} manager of a {@link Game}.
 */
public class GameTeamManager<T extends GameTeam> {

    private final Map<String, T> teams = new HashMap<>();
    private final Map<T, Team> teamsPair = new HashMap<>();
    private final Scoreboard scoreboard = Bukkit.getScoreboardManager().getNewScoreboard();

    public GameTeamManager() {}

    /**
     * Registers a team into this manager.
     * @param team the team
     */
    @ApiStatus.Internal
    public void registerTeam(@NotNull T team) {
        Preconditions.checkState(!teams.containsValue(team), "team '" + team.id() + "' already registered");
        Team bukkitTeam = scoreboard.registerNewTeam(team.id());
        bukkitTeam.color(team.getColor());
        bukkitTeam.displayName(team.componentDisplayName());
        teams.put(team.id(), team);
        teamsPair.put(team, bukkitTeam);
    }

    /**
     * Assigns a player to a random team. If the provided player is already in a team, that team will be returned.
     * @param player the player
     * @return the chosen team
     * @throws IllegalStateException if there are no teams registered
     */
    public T joinRandom(GamePlayer player) {
        Preconditions.checkState(!teams.isEmpty(), "no teams available");
        Optional<T> existingTeam = teams.values().stream()
                .filter(team -> team.getPlayers().contains(player))
                .findAny();
        if (existingTeam.isPresent())
            return existingTeam.get();
        List<T> teams = this.teams.values().stream().sorted().toList();
        T team = teams.get(Util.getRandomInt(teams.size() - 1));
        joinTeam(team, player);
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
        player.message("You have joined team " + SimmyGameAPI.mini().serialize(team.componentDisplayName()) + "<reset>!");
    }

    public @Nullable T getTeam(GamePlayer player) {
        return teams.values().stream()
                .filter(team -> team.getPlayers().contains(player))
                .findAny().orElse(null);
    }

    /**
     * @param color the team color for lookup
     * @return the team with associated color, otherwise null
     */
    public @Nullable T getTeam(NamedTextColor color) {
        return teams.values().stream()
                .filter(team -> team.getColor().equals(color))
                .findAny().orElse(null);
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

}
