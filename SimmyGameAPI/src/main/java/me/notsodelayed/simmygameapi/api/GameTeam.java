package me.notsodelayed.simmygameapi.api;

import java.util.HashSet;
import java.util.Set;
import java.util.stream.Collectors;

import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.TextComponent;
import net.kyori.adventure.text.format.NamedTextColor;
import org.apache.commons.lang3.StringUtils;
import org.bukkit.Location;
import org.bukkit.Sound;
import org.jetbrains.annotations.NotNull;

import me.notsodelayed.simmygameapi.SimmyGameAPI;
import me.notsodelayed.simmygameapi.api.util.Identifiable;

/**
 * Represents a team of a {@link Game}.
 */
public class GameTeam implements BaseTeam, Identifiable, Comparable<GameTeam> {

    private final String id;
    private final TextComponent displayName;
    private final NamedTextColor color;
    private final Set<GamePlayer> players;

    public GameTeam(@NotNull NamedTextColor color) {
        this(color, color.toString());
    }

    private GameTeam(@NotNull NamedTextColor color, @NotNull String id) {
        this.id = id;
        this.color = color;
        displayName = Component.text(StringUtils.capitalize(color.toString()), color);
        players = new HashSet<>();
    }

    public void dispatchMessage(Component message) {
        for (GamePlayer player : players)
            player.message(message);
    }

    /**
     * @param message the message (supported by MiniMessage)
     */
    public void dispatchMessage(String message) {
        dispatchMessage(SimmyGameAPI.mini().deserialize(message));
    }

    public void dispatchSound(Sound sound, float volume, float pitch) {
        for (GamePlayer player : players)
            player.playSound(sound, volume, pitch);
    }

    public void dispatchSound(Location location, Sound sound, float volume, float pitch) {
        for (GamePlayer player : players)
            player.playSound(location, sound, volume, pitch);
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
        return players.stream()
                .map(clazz::cast)
                .collect(Collectors.toUnmodifiableSet());

    }

    public void addPlayer(GamePlayer player) {
        players.add(player);
    }

    public void removePlayer(GamePlayer player) {
        players.remove(player);
    }

    public NamedTextColor getColor() {
        return color;
    }

    @Override
    public @NotNull String id() {
        return id;
    }

    /**
     * @return the component with the name formatted with {@link #getColor()}
     */
    public @NotNull TextComponent componentDisplayName() {
        return displayName;
    }

    /**
     * @return the string only version of {@link #componentDisplayName()}
     */
    @Override
    public String displayName() {
        return displayName.content();
    }

    @Override
    public int compareTo(@NotNull GameTeam other) {
        return Integer.compare(players.size(), other.players.size()) ;
    }

}
