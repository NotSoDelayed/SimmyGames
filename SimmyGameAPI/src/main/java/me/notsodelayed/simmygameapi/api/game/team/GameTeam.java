package me.notsodelayed.simmygameapi.api.game.team;

import java.util.HashSet;
import java.util.Set;
import java.util.stream.Collectors;

import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import org.bukkit.Sound;
import org.jetbrains.annotations.NotNull;

import me.notsodelayed.simmygameapi.api.game.Game;
import me.notsodelayed.simmygameapi.api.game.player.GamePlayer;
import me.notsodelayed.simmygameapi.api.game.player.TeamPlayer;
import me.notsodelayed.simmygameapi.util.StringUtil;

/**
 * Represents a team of a {@link Game}.
 */
public class GameTeam implements BaseTeam {

    private final String id;
    private final Component displayName;
    private final NamedTextColor color;
    private final Set<TeamPlayer<?>> players;

    public GameTeam(@NotNull NamedTextColor color) {
        this(color, color.toString());
    }

    private GameTeam(@NotNull NamedTextColor color, @NotNull String id) {
        this.id = id;
        this.color = color;
        displayName = Component.text(color.toString())
                .color(color);
        players = new HashSet<>();
    }

    /**
     * Sends a {@link org.bukkit.ChatColor} formatted message to the team members.
     * @param message the message
     */
    public void message(String message) {
        for (TeamPlayer<?> player : players)
            player.message(StringUtil.color(message));
    }

    public void message(Component message) {
        for (TeamPlayer<?> player : players)
            player.message(message);
    }

    public void dispatchSound(Sound sound, int volume, int pitch) {
        for (TeamPlayer<?> player : players) {
            player.playSound(sound, volume, pitch);
        }
    }

    /**
     * @return an immutable set of the players
     */
    @Override
    public Set<? extends TeamPlayer<?>> getPlayers() {
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

    public void addPlayer(TeamPlayer<?> player) {
        players.add(player);
    }

    public void removePlayer(TeamPlayer<?> player) {
        players.remove(player);
    }

    public NamedTextColor getColor() {
        return color;
    }

    public Component getDisplayName() {
        return displayName;
    }

    public String getId() {
        return id;
    }

}
