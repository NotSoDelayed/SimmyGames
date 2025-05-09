package me.notsodelayed.simmygameapi.api.player;

import org.jetbrains.annotations.Nullable;

import me.notsodelayed.simmygameapi.api.BasePlayer;
import me.notsodelayed.simmygameapi.api.kit.GameKit;

public interface KitPlayer<K extends GameKit> extends BasePlayer {

    @Nullable K getKit();

    /**
     * @return the kit to be assigned next
     */
    @Nullable K getNextKit();

    /**
     * Sets the kit to be queried.
     * @param kit the next kit
     */
    void setNextKit(@Nullable K kit);

    /**
     * Assigns {@link #getNextKit()} as {@link #getKit()} to the player with its contents.
     * @return the assigned kit if {@link #getNextKit()} is <b>not null</b> and <b>!= {@link #getKit()}</b>, otherwise null
     */
    @Nullable K assignNextKit();

    /**
     * Gives the {@link #getKit() current kit of this player} to them.
     * @throws IllegalStateException if this player is offline
     */
    default void giveCurrentKit() {
        if (getKit() == null)
            return;
        if (asBukkitPlayer() == null)
            throw new IllegalStateException("player is offline");
        getKit().give(asBukkitPlayer());
    }

}
