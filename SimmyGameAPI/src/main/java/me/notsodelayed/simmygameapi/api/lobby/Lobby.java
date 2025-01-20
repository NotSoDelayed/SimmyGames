package me.notsodelayed.simmygameapi.api.lobby;

public class Lobby {

    private final LobbyMap map;

    public Lobby(LobbyMap map) {
        this.map = map;
    }

    public LobbyMap getMap() {
        return map;
    }

}
