package me.notsodelayed.simmygameapi.api.game;

import me.notsodelayed.simmygameapi.api.game.team.GameTeam;

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
    }

    T getTeamAlpha();
    T getTeamBeta();
    void setTeamAlpha(T team);
    void setTeamBeta(T team);

}
