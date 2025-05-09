package me.notsodelayed.thenexus.event;

import me.notsodelayed.simmygameapi.api.Game;
import me.notsodelayed.simmygameapi.api.event.PlayerEvent;
import me.notsodelayed.thenexus.NexusMap;
import me.notsodelayed.thenexus.entity.Nexus;
import me.notsodelayed.thenexus.game.NexusGame;
import me.notsodelayed.thenexus.game.NexusPlayer;
import me.notsodelayed.thenexus.team.NexusTeam;

public abstract class NexusEvent extends PlayerEvent {

    private final Nexus nexus;

    protected NexusEvent(Game game, NexusPlayer player, Nexus nexus) {
        super(game, player);
        this.nexus = nexus;
    }

    @Override
    public NexusGame<? extends NexusMap, ? extends NexusTeam> getGame() {
        return (NexusGame<? extends NexusMap, ? extends NexusTeam>) super.getGame();
    }

    @Override
    public NexusPlayer getPlayer() {
        return (NexusPlayer) player;
    }

    public Nexus getNexus() {
        return nexus;
    }

}
