package me.notsodelayed.thenexus.event;

import me.notsodelayed.thenexus.game.NexusGame;
import org.bukkit.event.Event;

/**
 * Represents a {@link NexusGame} event.
 */
public abstract class NexusGameEvent extends Event {

    private NexusGame game;

    protected NexusGameEvent(NexusGame game) {
        this.game = game;
    }

    /**
     * @return the game
     */
    public NexusGame getGame() {
        return game;
    }

}
