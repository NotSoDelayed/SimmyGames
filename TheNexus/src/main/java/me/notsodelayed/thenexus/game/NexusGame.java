package me.notsodelayed.thenexus.game;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import me.notsodelayed.simmygameapi.api.game.TeamVsTeamGame;
import me.notsodelayed.simmygameapi.api.map.MapChoice;
import me.notsodelayed.simmygameapi.util.LoggerUtil;
import me.notsodelayed.thenexus.entity.NexusPlayer;
import me.notsodelayed.thenexus.entity.game.Nexus;
import me.notsodelayed.thenexus.entity.team.NexusTeam;
import org.bukkit.ChatColor;
import org.bukkit.block.Block;
import org.jetbrains.annotations.ApiStatus;
import org.jetbrains.annotations.NotNull;

public class NexusGame<T extends NexusTeam, P extends NexusPlayer> extends TeamVsTeamGame<NexusTeam, NexusPlayer> {

    private final Map<NexusTeam, Nexus> teamNexusMap = new HashMap<>();
    private static final String[] KIT_TYPES = new String[] {
            "classic", "potion", "trigger-potion"
    };

    public NexusGame(int minPlayers, int maxPlayers) {
        super(minPlayers, maxPlayers, new MapChoice(null),
                new NexusTeam("red", ChatColor.RED), // teamAlpha
                new NexusTeam("blue", ChatColor.BLUE)); // teamBeta
    }

    @Override
    public void tick() {
        this.getPlayers().forEach(nexusPlayer -> {
            Optional.ofNullable(nexusPlayer.getKit()).ifPresentOrElse(nexusKit -> {
               if (!nexusPlayer.applyKit())
                   LoggerUtil.verbose(this, nexusPlayer.getName() + " failed to receive kit " + nexusKit.getClass().getTypeName() + " " + nexusKit.getId());
            }, () -> LoggerUtil.verbose(this, nexusPlayer.getName() + " does not have a kit assigned. (Unexpected behaviour)"));
        });
    }

    @Override
    protected void delete() {
        super.delete();
        NexusGameManager.get().removeGame((NexusGame<NexusTeam, NexusPlayer>) this);
    }

    /**
     * @param team the nexus team, <b>specifically</b> {@link TeamVsTeamGame#getTeamAlpha()} or {@link TeamVsTeamGame#getTeamBeta()}
     * @return the nexus associated to the team
     * @throws IllegalArgumentException if provided team is not associated to this game
     */
    @NotNull
    public Nexus getNexus(NexusTeam team) {
        if (!teamNexusMap.containsKey(team))
            throw new IllegalArgumentException("Called with unassociated team " + team);
        return teamNexusMap.get(team);
    }

    public Nexus getNexus(Block block) {

    }

    /**
     * @return the kit types of this game mode, usually used for kit registrations
     */
    @ApiStatus.Internal
    public static String[] getKitTypes() {
        return KIT_TYPES.clone();
    }

    @Override
    public String toString() {
        return "NexusGame{" +
                "nexus=" + teamNexusMap +
                ", parent:[{" + super.toString() + "}]}";
    }

}
