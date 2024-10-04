package me.notsodelayed.simmygameapi.api.game.team;

import java.util.Set;

import me.notsodelayed.simmygameapi.api.game.player.GamePlayer;
import me.notsodelayed.simmygameapi.api.game.player.TeamPlayer;

public interface BaseTeam {

    Set<? extends TeamPlayer<?>> getPlayers();

}
