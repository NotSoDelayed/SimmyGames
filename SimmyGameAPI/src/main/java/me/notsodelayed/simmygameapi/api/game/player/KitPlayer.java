package me.notsodelayed.simmygameapi.api.game.player;

import java.util.function.Predicate;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import me.notsodelayed.simmygameapi.api.game.Game;
import me.notsodelayed.simmygameapi.api.game.MapGame;
import me.notsodelayed.simmygameapi.api.game.kit.GameKit;

public interface KitPlayer<K extends GameKit> extends BasePlayer {

    @Nullable K getKit();

    /**
     * Sets the kit to be assigned next.
     * <p>This method handles whether the new kit should is safe to be assigned on the fly, or queue it for later, via {@link #assignKitPredicate()}</p>
     * <p></p>
     * @param kit the kit to be assigned next
     */
    void assignNextKit(K kit);

    /**
     * The default predicate to determine whether the kit is safe to be assigned on the fly, or queue it for later.
     * @return the predicate for {@link #assignNextKit(GameKit)}
     */
    default Predicate<KitPlayer<K>> assignKitPredicate() {
        if (getPlayer() == null)
            return kKitPlayer -> false;
        return kitPlayer -> {
            Game game = getGame();
            if (game.hasBegun())
                return true;
            if (game instanceof MapGame<?> mapGame)
                return getPlayer().getWorld().getName().equals(mapGame.getWorldName());
            return false;
        };
    }

    /**
     * @return the kit to be assigned next
     */
    @Nullable K getNextKit();

    /**
     * Gives the {@link #getKit() current kit of this player} to them. Does nothing if <b>{@link #getKit()} = null</b>.
     * @throws IllegalStateException if this player is offline
     */
    default void giveCurrentKit() {
        if (getPlayer() == null)
            throw new IllegalStateException("player is offline");
        getKit().getItems().forEach((index, itemStack) ->
                getPlayer().getInventory().setItem(index, itemStack));
    }

}
