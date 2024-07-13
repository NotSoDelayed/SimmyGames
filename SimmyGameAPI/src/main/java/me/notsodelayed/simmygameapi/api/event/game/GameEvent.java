package me.notsodelayed.simmygameapi.api.event.game;

import me.notsodelayed.simmygameapi.api.game.Game;
import org.bukkit.Bukkit;
import org.bukkit.event.Event;
import org.jetbrains.annotations.NotNull;

/**
 * The base class for {@link Game} events.
 */
public abstract class GameEvent extends Event {

    private final Game game;

    protected GameEvent(@NotNull Game game) {
        super(!Bukkit.isPrimaryThread());
        this.game = game;
    }

    /**
     * @return the game
     */
    public Game getGame() {
        return game;
    }

}
