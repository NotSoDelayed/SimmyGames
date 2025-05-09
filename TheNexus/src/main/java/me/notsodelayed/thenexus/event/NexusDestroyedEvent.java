package me.notsodelayed.thenexus.event;

import org.bukkit.event.HandlerList;
import org.jetbrains.annotations.NotNull;

import me.notsodelayed.thenexus.NexusMap;
import me.notsodelayed.thenexus.entity.Nexus;
import me.notsodelayed.thenexus.game.NexusGame;
import me.notsodelayed.thenexus.game.NexusPlayer;
import me.notsodelayed.thenexus.team.NexusTeam;

public class NexusDestroyedEvent extends NexusDamagedEvent {

    private static final HandlerList HANDLERS = new HandlerList();
    private final NexusTeam loser;

    public NexusDestroyedEvent(NexusGame<? extends NexusMap, ? extends NexusTeam> game, NexusPlayer player, Nexus nexus, NexusTeam loser) {
        super(game, player, nexus);
        this.loser = loser;
    }

    /**
     * @return the team of the destroyed nexus
     */
    public NexusTeam getLoser() {
        return loser;
    }

    public static HandlerList getHandlerList() {
        return HANDLERS;
    }

    @Override
    public @NotNull HandlerList getHandlers() {
        return HANDLERS;
    }

}
