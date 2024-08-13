package me.notsodelayed.simmygameapi.api.game.player;

import me.notsodelayed.simmygameapi.api.game.team.GameTeam;

public interface TeamPlayer<T extends GameTeam> extends BasePlayer {

    T getTeam();

}
