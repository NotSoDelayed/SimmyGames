package me.notsodelayed.thenexus.game;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;
import java.util.Set;

import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockBreakEvent;

import me.notsodelayed.simmygameapi.api.game.GameState;
import me.notsodelayed.simmygameapi.api.game.MapGame;
import me.notsodelayed.simmygameapi.api.game.TeamVsTeamGame;
import me.notsodelayed.simmygameapi.api.map.GameMap;
import me.notsodelayed.simmygameapi.api.player.GamePlayer;
import me.notsodelayed.simmygameapi.api.team.GameTeamManager;
import me.notsodelayed.thenexus.TheNexus;
import me.notsodelayed.thenexus.entity.Nexus;
import me.notsodelayed.thenexus.map.NexusMap;
import me.notsodelayed.thenexus.team.NexusTeam;

public abstract class NexusGame<M extends NexusMap, T extends NexusTeam> extends MapGame<M> implements TeamVsTeamGame<T> {

    private final GameTeamManager<T> teamManager;
    private final Map<T, Nexus> teamNexus = new HashMap<>();

    /**
     * Creates a MapGame without pre-defined {@link GameMap}.
     *
     * @param minPlayers the minimum player count
     * @param maxPlayers the maximum player count
     * @apiNote This returns a MapGame instance with state {@link GameState#LOADING}, where it is not joinable. <p>Developers must call {@link #ready()} in post-setup. </p>
     * @implNote Developers must ensure <b>{@link #getMap()} != null</b> before {@link #init()} is called.
     */
    protected NexusGame(int minPlayers, int maxPlayers) {
        super(minPlayers, maxPlayers);
        teamManager = new GameTeamManager<>();
    }

    protected void registerNexus(T team, Nexus nexus) {
        teamNexus.put(team, nexus);
    }

    public Nexus getTeamNexus(T team) {
        if (team != getTeamAlpha() && team != getTeamBeta())
            throw new IllegalArgumentException(team.getDisplayName() + " is not a part of " + getFormattedName());
        Nexus nexus = teamNexus.get(team);
        if (nexus == null)
            throw new IllegalArgumentException(team.getDisplayName() + " in " + getFormattedName() + " does not have a nexus");
        return nexus;
    }

    /**
     * @param nexus the nexus
     * @return the associated team
     * @throws
     */
    public T getNexusTeam(Nexus nexus) {
        Optional<T> team = teamNexus.entrySet().stream()
                .filter(entry -> entry.getValue().equals(nexus))
                .map(Map.Entry::getKey)
                .findFirst();
        if (team.isEmpty()) {
            TheNexus.logger.severe("Attempted to get team of unregistered nexus " + nexus.getLocation().toString() + "!");
            throw new IllegalArgumentException("provided nexus is not registered in game " + getFormattedName());
        }
        return team.get();
    }

    @Override
    public Set<NexusPlayer> getPlayers() {
        return getPlayers(NexusPlayer.class);
    }

    @Override
    public GameTeamManager<T> getTeamManager() {
        return teamManager;
    }

    static {
        // TODO register nexus listener only when game is active
        Bukkit.getPluginManager().registerEvents(new Listener() {
            @EventHandler
            public void nexusBreak(BlockBreakEvent event) {
                Block block = event.getBlock();
                if (!(block.getType() == Material.END_STONE))
                    return;
                // This shouldn't throw CCE
                GamePlayer gamePlayer = GamePlayer.get(event.getPlayer());
                if (!(gamePlayer instanceof NexusPlayer nexusPlayer))
                    return;
                NexusGame<NexusMap, NexusTeam> nexusGame = (NexusGame<NexusMap, NexusTeam>) nexusPlayer.getGame();
                // Gets the nexus of the breaker's team
                Nexus nexus = nexusGame.getTeamNexus(nexusPlayer.getTeam());
                // Checks whether the broken nexus is theirs
                if (nexus.getLocation().equals(block.getLocation()))
                    return;
                nexusGame.dispatchMessage(nexusPlayer.getName() + " broke nexus " + nexus.getLocation().toString());
            }
        }, TheNexus.instance);
    }

}
