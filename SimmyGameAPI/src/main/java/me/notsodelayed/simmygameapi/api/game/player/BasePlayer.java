package me.notsodelayed.simmygameapi.api.game.player;

import java.util.Optional;
import java.util.UUID;

import org.bukkit.Bukkit;
import org.bukkit.OfflinePlayer;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.Nullable;

public interface BasePlayer {

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
     * @return the online player
     */
    @Nullable
    default Player getPlayer() {
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
