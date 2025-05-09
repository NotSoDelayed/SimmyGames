package me.notsodelayed.simmygameapi.api.game;

import me.notsodelayed.simmygameapi.api.BaseGame;
import me.notsodelayed.simmygameapi.api.GameTeam;
import me.notsodelayed.simmygameapi.api.team.GameTeamManager;

public interface TeamGame<T extends GameTeam> extends BaseGame {

    GameTeamManager<T> getTeamManager();

}
