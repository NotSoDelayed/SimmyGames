package me.notsodelayed.thenexus.event;

import org.bukkit.event.Cancellable;
import org.bukkit.event.HandlerList;
import org.jetbrains.annotations.NotNull;

import me.notsodelayed.thenexus.entity.game.Nexus;
import me.notsodelayed.thenexus.game.NexusGame;

/**
 * Called when a {@link Nexus} is being damaged.
 */
public class NexusDamageEvent extends NexusGameEvent implements Cancellable {

    private static final HandlerList HANDLER_LIST = new HandlerList();
    private final Nexus nexus;
    private boolean cancelled = false;

    protected NexusDamageEvent(NexusGame game, Nexus nexus) {
        super(game);
        this.nexus = nexus;
    }

    /**
     * @return the nexus
     */
    public Nexus getNexus() {
        return nexus;
    }

    public static HandlerList getHandlerList() {
        return HANDLER_LIST;
    }

    @Override
    public @NotNull HandlerList getHandlers() {
        return HANDLER_LIST;
    }

    @Override
    public boolean isCancelled() {
        return cancelled;
    }

    @Override
    public void setCancelled(boolean cancelled) {
        this.cancelled = cancelled;
    }

}
