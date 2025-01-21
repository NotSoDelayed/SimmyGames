package me.notsodelayed.thenexus.event;

import org.bukkit.event.HandlerList;
import org.jetbrains.annotations.NotNull;

import me.notsodelayed.simmygameapi.api.event.GameEvent;
import me.notsodelayed.simmygameapi.api.game.Game;
import me.notsodelayed.thenexus.entity.Nexus;

public class NexusDestroyedEvent extends GameEvent {

    private static final HandlerList HANDLERS = new HandlerList();

    private final Nexus nexus;

    public NexusDestroyedEvent(Game game, Nexus nexus) {
        super(game);
        this.nexus = nexus;
    }

    public Nexus getNexus() {
        return nexus;
    }

    @Override
    public @NotNull HandlerList getHandlers() {
        return HANDLERS;
    }

}
