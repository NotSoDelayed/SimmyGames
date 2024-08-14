package me.notsodelayed.thenexus.event;

import org.bukkit.event.Event;

import me.notsodelayed.thenexus.game.NexusGame;

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
