package me.notsodelayed.simmygameapi.api.game;

import me.notsodelayed.simmygameapi.api.game.team.GameTeam;
import me.notsodelayed.simmygameapi.api.game.team.GameTeamManager;

public interface TeamGame<T extends GameTeam> extends BaseGame {

    GameTeamManager<T> getTeamManager();

}
