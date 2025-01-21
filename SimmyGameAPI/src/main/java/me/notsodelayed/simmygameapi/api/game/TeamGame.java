package me.notsodelayed.simmygameapi.api.game;

import me.notsodelayed.simmygameapi.SimmyGameAPI;
import me.notsodelayed.simmygameapi.api.BaseGame;
import me.notsodelayed.simmygameapi.api.player.GamePlayer;
import me.notsodelayed.simmygameapi.api.player.TeamPlayer;
import me.notsodelayed.simmygameapi.api.team.GameTeam;
import me.notsodelayed.simmygameapi.api.team.GameTeamManager;

public interface TeamGame<T extends GameTeam> extends BaseGame {

    GameTeamManager<T> getTeamManager();

//    @Override
//    default void init() {
//        for (GamePlayer gamePlayer : getPlayers()) {
//            // CCE would happen due to faulty implementation by sub-games
//            TeamPlayer<T> teamPlayer = (TeamPlayer<T>) gamePlayer;
//            if (teamPlayer.getTeam() == null) {
//                T team = getTeamManager().joinRandom(teamPlayer);
//                teamPlayer.message("Placed you into " + SimmyGameAPI.miniMessage().serialize(team.getDisplayName()) + "<reset> team!");
//            }
//        }
//    }

}
