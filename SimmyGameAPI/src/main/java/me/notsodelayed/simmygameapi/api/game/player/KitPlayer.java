package me.notsodelayed.simmygameapi.api.game.player;

import java.util.function.Predicate;

import me.notsodelayed.simmygameapi.api.game.kit.GameKit;

public interface KitPlayer<K extends GameKit> extends BasePlayer {

    K getKit();
    void setKit(K kit);

    K getQueriedKit();
    void setQueriedKit(K kit);

    /**
     * Applies {@link #getKit()} to this player. Does nothing if <b>{@link #getKit()} = null</b>.
     * @throws IllegalStateException if this player is offline
     */
    default void applyKit() {
        getOptionalPlayer().ifPresentOrElse(player ->
                getKit().getItems().forEach((index, itemStack) ->
                        player.getInventory().setItem(index, itemStack)),
                () -> {
            throw new IllegalStateException("player is offline");
        });
    }

    /**
     * Sets the kit of this player to the queried kit
     * @param predicate the predicate for assigning the queried kit
     */
    default void applyQueriedKit(Predicate<KitPlayer<K>> predicate) {
        if (getQueriedKit() != null && predicate.test(this))
            setKit(getQueriedKit());
    }

}
