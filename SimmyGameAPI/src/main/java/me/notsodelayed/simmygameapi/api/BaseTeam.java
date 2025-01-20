package me.notsodelayed.simmygameapi.api;

import java.util.Set;

import me.notsodelayed.simmygameapi.api.player.TeamPlayer;

public interface BaseTeam {

    Set<? extends TeamPlayer<?>> getPlayers();

}
