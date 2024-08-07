package me.notsodelayed.simmygameapi.api.game.team;

import java.util.Set;

import me.notsodelayed.simmygameapi.api.game.player.GamePlayer;

public interface BaseTeam {

    Set<? extends GamePlayer> getPlayers();

}
