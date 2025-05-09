package me.notsodelayed.thenexus.event;

import org.bukkit.event.HandlerList;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import me.notsodelayed.simmygameapi.api.Game;
import me.notsodelayed.thenexus.entity.Nexus;
import me.notsodelayed.thenexus.game.NexusPlayer;

/**
 * An event that fires whenever a {@link Nexus} has been damaged.
 */
public class NexusDamagedEvent extends NexusEvent {

    private static final HandlerList HANDLERS = new HandlerList();

    public NexusDamagedEvent(Game game, @Nullable NexusPlayer attacker, Nexus nexus) {
        super(game, attacker, nexus);
    }

    @Override
    public @NotNull HandlerList getHandlers() {
        return HANDLERS;
    }

    public static HandlerList getHandlerList() {
        return HANDLERS;
    }

}
