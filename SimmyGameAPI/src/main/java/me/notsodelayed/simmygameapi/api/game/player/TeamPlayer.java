package me.notsodelayed.simmygameapi.api.game.player;

import org.jetbrains.annotations.Nullable;

import me.notsodelayed.simmygameapi.api.game.TeamGame;
import me.notsodelayed.simmygameapi.api.game.team.GameTeam;

public interface TeamPlayer<T extends GameTeam> extends BasePlayer {

    @Nullable T getTeam();

}
