package me.notsodelayed.thenexus.game;

public class TntNexusGame extends NexusGame {

    protected TntNexusGame(int minPlayers, int maxPlayers) {
        super(minPlayers, maxPlayers);
    }

    @Override
    public void tick() {
        super.tick();
        // TODO initiate game timers, virtual chests, triple nexus mechanics etc
    }

}
