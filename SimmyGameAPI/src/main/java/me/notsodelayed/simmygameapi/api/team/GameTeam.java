package me.notsodelayed.simmygameapi.api.team;

import java.util.HashSet;
import java.util.Locale;
import java.util.Set;
import java.util.UUID;

import me.notsodelayed.simmygameapi.SimmyGameAPI;
import me.notsodelayed.simmygameapi.api.entity.GamePlayer;
import me.notsodelayed.simmygameapi.util.StringUtil;
import org.apache.commons.lang3.StringUtils;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Sound;
import org.bukkit.SoundCategory;
import org.bukkit.entity.Player;
import org.bukkit.scoreboard.Team;
import org.jetbrains.annotations.ApiStatus;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

/**
 * Represents a team of a game.
 */
@SuppressWarnings("deprecation")
public class GameTeam {

    private final Team team;
    private final Set<GamePlayer> players = new HashSet<>();

    public GameTeam(@NotNull String id, @NotNull ChatColor color) {
        this(id, StringUtils.capitalize(id), color);
    }

    public GameTeam(@NotNull String id, @Nullable String displayName, @NotNull ChatColor color) {
        // Can't have duplicate id in Bukkit team
        team = SimmyGameAPI.instance.getScoreboard().registerNewTeam(UUID.randomUUID() + "-" + id);
        team.setDisplayName(displayName);
        team.setColor(color);
    }

    /**
     * @param message the message to dispatch to all game players
     */
    public void dispatchMessage(String message) {
        for (GamePlayer player : players) {
            player.message(StringUtil.color(message));
        }
    }

    /**
     * @param sound the sound
     * @param volume the volume
     * @param pitch the pitch
     */
    public void dispatchSound(Sound sound, int volume, int pitch) {
        for (GamePlayer player : players) {
            player.playSound(sound, volume, pitch);
        }
    }

    /**
     * @return an immutable set of the players
     */
    public Set<? extends GamePlayer> getPlayers() {
        return Set.copyOf(players);
    }

    /**
     * @param player the player
     * @return whether the player is successfully added to the team
     */
    public boolean addPlayer(GamePlayer player) {
        return players.add(player);
    }

    /**
     * @param player the player
     * @return whether the player is successfully removed from the team
     */
    public boolean removePlayer(GamePlayer player) {
        return players.remove(player);
    }

    /**
     * @return the bukkit team associated
     */
    public Team getBukkitTeam() {
        return team;
    }

    /**
     * @return the color
     */
    public ChatColor getColor() {
        return team.getColor();
    }

    /**
     * @return the id
     */
    public String getId() {
        return team.getName();
    }

    /**
     * @return the string formatted for displaying
     */
    public String getDisplayName() {
        return team.getColor() + team.getDisplayName();
    }

}
