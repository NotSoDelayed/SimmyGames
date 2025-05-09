package me.notsodelayed.simmygameapi.api.event;

import me.notsodelayed.simmygameapi.api.Game;
import me.notsodelayed.simmygameapi.api.GamePlayer;

public abstract class PlayerEvent extends GameEvent {

    protected final GamePlayer player;

    protected PlayerEvent(Game game, GamePlayer player) {
        super(game);
        this.player = player;
    }

    public GamePlayer getPlayer() {
        return player;
    }

}
