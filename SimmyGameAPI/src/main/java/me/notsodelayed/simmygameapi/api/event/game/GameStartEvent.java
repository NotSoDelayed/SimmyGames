package me.notsodelayed.simmygameapi.api.event.game;

import me.notsodelayed.simmygameapi.api.game.Game;
import org.bukkit.event.HandlerList;
import org.jetbrains.annotations.NotNull;

/**
 * Called when a {@link Game} has started, usually after the game start countdown.
 */
public class GameStartEvent extends GameEvent {

    public GameStartEvent(@NotNull Game game) {
        super(game);
    }

    private static final HandlerList HANDLER_LIST = new HandlerList();

    @Override
    public @NotNull HandlerList getHandlers() {
        return HANDLER_LIST;
    }

}
