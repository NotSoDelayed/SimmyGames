package me.notsodelayed.simmygameapi.api.game.event;

import me.notsodelayed.simmygameapi.api.game.Game;
import org.bukkit.event.HandlerList;
import org.jetbrains.annotations.NotNull;

/**
 * Called when a new {@link Game} has been created successfully.
 */
public class GameCreatedEvent extends GameEvent {

    private static final HandlerList HANDLER_LIST = new HandlerList();

    public GameCreatedEvent(@NotNull Game game) {
        super(game);
    }

    public static HandlerList getHandlerList() {
        return HANDLER_LIST;
    }

    @Override
    public @NotNull HandlerList getHandlers() {
        return HANDLER_LIST;
    }

}
