package me.notsodelayed.simmygameapi.api.game;

import me.notsodelayed.simmygameapi.api.team.GameTeam;

public interface TeamVsTeamGame<T extends GameTeam> extends TeamGame<T> {

    @Override
    default void ready() throws IllegalStateException {
        String[] exception = new String[2];
        if (getTeamAlpha() == null)
            exception[0] = "team alpha is null";
        if (getTeamBeta() == null)
            exception[1] = "team beta is null";
        if (exception[0] == null || exception[1] == null)
            throw new NullPointerException(String.join("; ", exception));
        TeamGame.super.ready();
    }

    /**
     * @param team the team
     * @return its rival team
     * @throws IllegalArgumentException if the team input is neither alpha nor beta
     */
    default T getRivalTeam(T team) {
        if (team == getTeamAlpha()) {
            return getTeamBeta();
        } else if (team == getTeamBeta()) {
            return getTeamAlpha();
        } else {
            throw new IllegalArgumentException("team is neither alpha nor beta");
        }
    }

    T getTeamAlpha();
    T getTeamBeta();

}
