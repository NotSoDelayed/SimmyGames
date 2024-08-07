package me.notsodelayed.simmygameapi.api.game.event;

import me.notsodelayed.simmygameapi.api.game.Game;
import me.notsodelayed.simmygameapi.api.game.GameState;
import org.bukkit.event.HandlerList;
import org.jetbrains.annotations.NotNull;

/**
 * Called when the {@link GameState} of a {@link Game} has been changed.
 */
public class GameStateChangeEvent extends GameEvent {

    private static final HandlerList HANDLER_LIST = new HandlerList();

    private final GameState gameState;

    protected GameStateChangeEvent(Game game, GameState gameState) {
        super(game);
        this.gameState = gameState;
    }

    /**
     * @return the game state
     */
    public GameState getGameState() {
        return gameState;
    }

    public static HandlerList getHandlerList() {
        return HANDLER_LIST;
    }

    @Override
    public @NotNull HandlerList getHandlers() {
        return HANDLER_LIST;
    }

}
