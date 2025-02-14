package me.notsodelayed.thenexus.game;

import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import me.notsodelayed.simmygameapi.api.GamePlayer;
import me.notsodelayed.simmygameapi.api.player.KitPlayer;
import me.notsodelayed.simmygameapi.api.player.StatisticsPlayer;
import me.notsodelayed.simmygameapi.api.player.TeamPlayer;
import me.notsodelayed.simmygameapi.api.statistics.Statistics;
import me.notsodelayed.thenexus.kit.NexusKit;
import me.notsodelayed.thenexus.map.NexusMap;
import me.notsodelayed.thenexus.team.NexusTeam;

/**
 * Represents a player of {@link NexusGame}
 */
public class NexusPlayer extends GamePlayer implements StatisticsPlayer, TeamPlayer<NexusTeam>, KitPlayer<NexusKit> {

    private @Nullable NexusKit kit = null, nextKit = null;
    private final Statistics stats = new Statistics();

    public NexusPlayer(Player player, NexusGame<?, ?> game) {
        super(player, game);
    }

    @Override
    public @Nullable NexusTeam getTeam() {
        NexusGame<? extends NexusMap, ? extends NexusTeam> game = (NexusGame<? extends NexusMap, ? extends NexusTeam>) getGame();
        return game.getTeamManager().getTeam(this);
    }

    @Override
    public @Nullable NexusKit getKit() {
        return kit;
    }

    @Override
    public @Nullable NexusKit getNextKit() {
        return nextKit;
    }

    @Override
    public void setNextKit(@Nullable NexusKit kit) {
        nextKit = kit;
    }

    @Override
    public @Nullable NexusKit assignNextKit() {
        return null;
    }

    @Override
    public @NotNull Statistics getStatistics() {
        return stats;
    }

}
