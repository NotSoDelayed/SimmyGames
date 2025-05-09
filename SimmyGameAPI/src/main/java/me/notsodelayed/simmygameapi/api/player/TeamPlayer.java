package me.notsodelayed.simmygameapi.api.player;

import org.jetbrains.annotations.Nullable;

import me.notsodelayed.simmygameapi.api.BasePlayer;
import me.notsodelayed.simmygameapi.api.GameTeam;

public interface TeamPlayer<T extends GameTeam> extends BasePlayer {

    @Nullable T getTeam();

}
