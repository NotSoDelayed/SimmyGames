package me.notsodelayed.simmygameapi.api.player;

import org.jetbrains.annotations.NotNull;

import me.notsodelayed.simmygameapi.api.BasePlayer;
import me.notsodelayed.simmygameapi.api.game.Game;
import me.notsodelayed.simmygameapi.api.statistics.Statistics;

/**
 * Represents a player with statistics of a {@link Game}.
 */
public interface StatisticsPlayer extends BasePlayer {

    @NotNull Statistics getStatistics();

}
