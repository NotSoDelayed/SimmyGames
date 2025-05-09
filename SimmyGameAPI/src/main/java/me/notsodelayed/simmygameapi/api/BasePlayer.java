package me.notsodelayed.simmygameapi.api;

import java.util.UUID;
import java.util.concurrent.CompletableFuture;

import net.kyori.adventure.text.Component;
import net.kyori.adventure.title.Title;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.OfflinePlayer;
import org.bukkit.Sound;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.jetbrains.annotations.NotNull;

import me.notsodelayed.simmygameapi.SimmyGameAPI;

/**
 * Foundation for {@link GamePlayer}.
 */
public interface BasePlayer {

    default void message(@NotNull Component message) {
        asBukkitPlayer().sendMessage(message);
    }

    /**
     * @param message the message (supported by MiniMessage)
     */
    default void message(@NotNull String message) {
        message(SimmyGameAPI.mini().deserialize(message));
    }

    default void actionbar(@NotNull Component message) {
        asBukkitPlayer().sendActionBar(message);
    }

    /**
     * @param message the message (supported by MiniMessage)
     */
    default void actionbar(@NotNull String message) {
        actionbar(SimmyGameAPI.mini().deserialize(message));
    }

    default void title(Title title) {
        asBukkitPlayer().showTitle(title);
    }

    default void openInventory(Inventory inv) {
        Player player = asBukkitPlayer();
        player.closeInventory();
        player.openInventory(inv);
    }

    /**
     * @param sound the sound
     * @param volume the volume
     * @param pitch the pitch
     */
    default void playSound(Sound sound, float volume, float pitch) {
        asBukkitPlayer().playSound(asBukkitPlayer().getLocation(), sound, volume, pitch);
    }

    default void playSound(Location location, Sound sound, float volume, float pitch) {
        asBukkitPlayer().playSound(location, sound, volume, pitch);
    }

    default CompletableFuture<Boolean> teleport(Location location) {
        return asBukkitPlayer().teleportAsync(location);
    }

    /**
     * Leaves the current game. This also renders this GamePlayer instance {@link #isValid() invalid}.
     */
    void leaveGame();

    @NotNull Game getGame();

    /**
     * @return whether this instance is valid (invalid when the player left the current game, the server)
     */
    boolean isValid();

    /**
     * @return the bukkit offline player
     */
    default OfflinePlayer asOfflinePlayer() {
        return Bukkit.getOfflinePlayer(getUuid());
    }

    /**
     * @return the bukkit player
     * @throws IllegalStateException if the player is offline
     */
    default @NotNull Player asBukkitPlayer() throws IllegalStateException {
        Player player = asOfflinePlayer().getPlayer();
        if (player == null)
            throw new IllegalStateException("player is offline");
        return player;
    }

    default String getName() {
        return asOfflinePlayer().getName();
    }

    /**
     * @return {@link OfflinePlayer#getUniqueId()}
     */
    UUID getUuid();

}
