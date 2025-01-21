package me.notsodelayed.simmygameapi.api.event;

import org.bukkit.event.Event;

import me.notsodelayed.simmygameapi.api.game.Game;

public abstract class GameEvent extends Event {

    private final Game game;

    protected GameEvent(Game game) {
        this.game = game;
    }

    public Game getGame() {
        return game;
    }

}
