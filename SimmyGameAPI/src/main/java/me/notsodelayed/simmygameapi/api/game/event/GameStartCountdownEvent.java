package me.notsodelayed.simmygameapi.api.game.event;

import org.bukkit.event.HandlerList;
import org.jetbrains.annotations.NotNull;

import me.notsodelayed.simmygameapi.api.game.Game;

/**
 * Called when a {@link Game} begins to countdown, usually after meeting game requirements, or manually requested to start.
 */
public class GameStartCountdownEvent extends GameEvent {

    public enum StartCause {
        /**
         * When the game requirements have been met
         */
        GAME_REQUIREMENTS_MET,

        /**
         * When the game start countdown is called manually
         */
        MANUAL_REQUEST,

        /**
         * Fallback start cause
         */
        UNKNOWN
    }

    private static final HandlerList HANDLERS = new HandlerList();
    private final StartCause startCause;

    public GameStartCountdownEvent(@NotNull Game game, @NotNull StartCause startCause) {
        super(game);
        this.startCause = startCause;
    }

    /**
     * @return the reason of this game start countdown to trigger
     */
    public StartCause getStartCause() {
        return startCause;
    }

    @Override
    public @NotNull HandlerList getHandlers() {
        return HANDLERS;
    }

}
