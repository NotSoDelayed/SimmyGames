package me.notsodelayed.thenexus.entity.team;

import java.util.Set;
import java.util.stream.Collectors;

import me.notsodelayed.simmygameapi.api.entity.GamePlayer;
import me.notsodelayed.simmygameapi.api.team.GameTeam;
import me.notsodelayed.thenexus.entity.NexusPlayer;
import me.notsodelayed.thenexus.game.NexusGame;
import org.bukkit.ChatColor;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

@SuppressWarnings("deprecation")
public class NexusTeam extends GameTeam {

    public NexusTeam(@NotNull String id, @NotNull ChatColor color) {
        super(id, color);
    }

    /**
     * @return an immutable set of the players of the nexus team
     */
    @Override
    public Set<NexusPlayer> getPlayers() {
        return super.getPlayers().stream()
                .map(NexusPlayer.class::cast)
                .collect(Collectors.toSet());
    }

}
