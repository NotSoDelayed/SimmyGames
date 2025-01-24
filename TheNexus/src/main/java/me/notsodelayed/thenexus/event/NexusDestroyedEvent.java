package me.notsodelayed.thenexus.event;

import org.bukkit.event.HandlerList;
import org.jetbrains.annotations.NotNull;

import me.notsodelayed.simmygameapi.api.event.GameEvent;
import me.notsodelayed.thenexus.entity.Nexus;
import me.notsodelayed.thenexus.game.NexusGame;
import me.notsodelayed.thenexus.map.NexusMap;
import me.notsodelayed.thenexus.team.NexusTeam;

public class NexusDestroyedEvent extends GameEvent {

    private static final HandlerList HANDLERS = new HandlerList();

    private final Nexus nexus;
    private final NexusTeam loser;

    public NexusDestroyedEvent(NexusGame<? extends NexusMap, ? extends NexusTeam> game, NexusTeam loser, Nexus nexus) {
        super(game);
        this.loser = loser;
        this.nexus = nexus;
    }

    public NexusTeam getLoser() {
        return loser;
    }

    public Nexus getNexus() {
        return nexus;
    }

    public static HandlerList getHandlerList() {
        return HANDLERS;
    }

    @Override
    public @NotNull HandlerList getHandlers() {
        return HANDLERS;
    }

}
