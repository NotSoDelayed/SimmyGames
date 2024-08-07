package me.notsodelayed.simmygameapi.api.game.team;

import java.util.HashSet;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;

import me.notsodelayed.simmygameapi.api.game.Game;
import me.notsodelayed.simmygameapi.api.game.player.GamePlayer;
import me.notsodelayed.simmygameapi.util.StringUtil;
import org.apache.commons.lang3.StringUtils;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Sound;
import org.bukkit.scoreboard.Scoreboard;
import org.bukkit.scoreboard.Team;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

/**
 * Represents a team of a {@link Game}.
 */
@SuppressWarnings("deprecation")
public class GameTeam implements BaseTeam {

    private final Team team;
    private final Set<GamePlayer> players;
    protected final Scoreboard scoreboard;

    public GameTeam(@NotNull ChatColor color) {
        this(color, color.asBungee().getName(), StringUtils.upperCase(color.asBungee().getName().replace('_', ' ')));
    }

    public GameTeam(@NotNull ChatColor color, @Nullable String displayName) {
        this(color, color.asBungee().getName(), displayName);
    }

    private GameTeam(@NotNull ChatColor color, @NotNull String id, @Nullable String displayName) {
        players = new HashSet<>();
        scoreboard = Bukkit.getScoreboardManager().getNewScoreboard();
        team = scoreboard.registerNewTeam(id);
        team.setDisplayName(Optional.ofNullable(displayName).orElse(id));
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
    @Override
    public Set<? extends GamePlayer> getPlayers() {
        return Set.copyOf(players);
    }

    /**
     * Utility method for developers to return specific type of GamePlayer
     * @param clazz the type to return
     * @param <P> the type which extends from GamePlayer
     * @return an immutable set of GamePlayer of specified type
     * @throws ClassCastException if the object is not assignable to the provided class
     */
    protected <P extends GamePlayer> Set<P> getPlayers(Class<P> clazz) {
        return getPlayers().stream()
                .map(clazz::cast)
                .collect(Collectors.toUnmodifiableSet());

    }

    /**
     * @param player the player
     */
    public void addPlayer(GamePlayer player) {
        players.add(player);
    }

    /**
     * @param player the player
     */
    public void removePlayer(GamePlayer player) {
        players.remove(player);
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
