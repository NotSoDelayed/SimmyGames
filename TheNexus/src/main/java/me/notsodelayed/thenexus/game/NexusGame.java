package me.notsodelayed.thenexus.game;

import java.util.HashMap;
import java.util.Map;
import java.util.Set;

import com.google.common.base.Preconditions;
import org.bukkit.block.Block;

import me.notsodelayed.simmygameapi.api.game.GameState;
import me.notsodelayed.simmygameapi.api.game.MapGame;
import me.notsodelayed.simmygameapi.api.game.TeamVsTeamGame;
import me.notsodelayed.simmygameapi.api.game.map.GameMap;
import me.notsodelayed.simmygameapi.api.game.team.GameTeamManager;
import me.notsodelayed.thenexus.entity.NexusPlayer;
import me.notsodelayed.thenexus.entity.game.Nexus;
import me.notsodelayed.thenexus.entity.team.NexusTeam;
import me.notsodelayed.thenexus.map.NexusMap;

public abstract class NexusGame extends MapGame<NexusMap> implements TeamVsTeamGame<NexusTeam> {

    private static final String[] KIT_TYPES = new String[] {
            "classic", "potion", "trigger-potion"
    };
    private final GameTeamManager<NexusTeam> teamManager;
    private final Map<NexusTeam, Nexus> teamNexusMap;
    private NexusTeam teamAlpha, teamBeta;

    /**
     * Creates a MapGame without pre-defined {@link GameMap}.
     *
     * @param minPlayers the minimum player count
     * @param maxPlayers the maximum player count
     * @apiNote This returns a MapGame instance with state {@link GameState#LOADING}, where it is not joinable. <p>Developers must call {@link #ready()} in post-setup. </p>
     * @implNote Developers must ensure <b>{@link #getMap()} != null</b> before {@link #tick()} is called.
     */
    protected NexusGame(int minPlayers, int maxPlayers) {
        super(minPlayers, maxPlayers);
        teamManager = new GameTeamManager<>();
        teamNexusMap = new HashMap<>();
    }

    @Override
    public Set<NexusPlayer> getPlayers() {
        return getPlayers(NexusPlayer.class);
    }

    /**
     * @param team the nexus team, <b>specifically</b> {@link TeamVsTeamGame#getTeamAlpha()} or {@link TeamVsTeamGame#getTeamBeta()}
     * @return the nexus associated to the team
     * @throws IllegalArgumentException if provided team is not associated to this game
     */
    public Nexus getNexus(NexusTeam team) {
        if (!teamNexusMap.containsKey(team))
            throw new IllegalArgumentException("Called with unassociated team " + team);
        return teamNexusMap.get(team);
    }

    /**
     * @param block the block
     * @return the nexus associated
     * @throws IllegalStateException if the game world is not loaded
     */
    public Nexus getNexus(Block block) {
        Preconditions.checkState(getWorld() != null, "game world is not loaded");
        return Nexus.get(block);
    }

    @Override
    public GameTeamManager<NexusTeam> getTeamManager() {
        return teamManager;
    }

    @Override
    public NexusTeam getTeamAlpha() {
        return teamAlpha;
    }

    @Override
    public void setTeamAlpha(NexusTeam teamAlpha) {
        Preconditions.checkState(isSetup(), "game is not in setup state");
        this.teamAlpha = teamAlpha;
    }

    @Override
    public NexusTeam getTeamBeta() {
        return teamBeta;
    }

    @Override
    public void setTeamBeta(NexusTeam teamBeta) {
        Preconditions.checkState(isSetup(), "game is not in setup state");
        this.teamBeta = teamBeta;
    }

}
