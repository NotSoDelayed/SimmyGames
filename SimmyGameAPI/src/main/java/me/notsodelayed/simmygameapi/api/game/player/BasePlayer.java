package me.notsodelayed.simmygameapi.api.game.player;

import java.util.Optional;
import java.util.UUID;

import net.kyori.adventure.text.Component;
import net.kyori.adventure.title.Title;
import org.bukkit.Bukkit;
import org.bukkit.OfflinePlayer;
import org.bukkit.Sound;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import me.notsodelayed.simmygameapi.api.game.Game;
import me.notsodelayed.simmygameapi.util.StringUtil;

public interface BasePlayer {

    /**
     * Sends a {@link org.bukkit.ChatColor} formatted message to this player.
     * @param message the message
     */
    default void message(@NotNull String message) {
        if (getPlayer() != null)
            getPlayer().sendMessage(StringUtil.color(message));
    }

    default void message(@NotNull Component message) {
        if (getPlayer() != null)
            getPlayer().sendMessage(message);
    }

    /**
     * Sends a {@link org.bukkit.ChatColor} formatted action bar to this player.
     * @param message the message
     */
    default void actionbar(@NotNull String message) {
        if (getPlayer() != null)
            getPlayer().sendActionBar(StringUtil.color(message));
    }

    default void actionbar(@NotNull Component message) {
        if (getPlayer() != null)
            getPlayer().sendActionBar(message);
    }

    default void title(Title title) {
        if (getPlayer() != null)
            getPlayer().showTitle(title);
    }

    /**
     * @param sound the sound
     * @param volume the volume
     * @param pitch the pitch
     */
    default void playSound(Sound sound, int volume, int pitch) {
        if (getPlayer() != null)
            getPlayer().playSound(getPlayer().getLocation(), sound, volume, pitch);
    }

    @NotNull Game getGame();

    /**
     * @return whether this instance is outdated (i.e. when the player left the current game, the server)
     */
    boolean isOutdated();

    /**
     * @return the bukkit offline player
     */
    default OfflinePlayer getBukkitPlayer() {
        return Bukkit.getOfflinePlayer(getUuid());
    }

    /**
     * @return the optional online player
     */
    default Optional<Player> getOptionalPlayer() {
        return Optional.ofNullable(getBukkitPlayer().getPlayer());
    }

    /**
     * @return the online player, if this player is online
     */
    default @Nullable Player getPlayer() {
        return getBukkitPlayer().getPlayer();
    }

    default String getName() {
        return getBukkitPlayer().getName();
    }

    /**
     * @return {@link OfflinePlayer#getUniqueId()}
     */
    UUID getUuid();

}
