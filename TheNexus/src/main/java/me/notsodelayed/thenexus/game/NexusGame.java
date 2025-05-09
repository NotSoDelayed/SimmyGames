package me.notsodelayed.thenexus.game;

import java.util.*;

import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.Sound;
import org.bukkit.block.Block;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockBreakEvent;

import me.notsodelayed.simmygameapi.SimmyGameAPI;
import me.notsodelayed.simmygameapi.api.GamePlayer;
import me.notsodelayed.simmygameapi.api.GameState;
import me.notsodelayed.simmygameapi.api.game.MapGame;
import me.notsodelayed.simmygameapi.api.game.TeamVsTeamGame;
import me.notsodelayed.simmygameapi.api.map.FixedMap;
import me.notsodelayed.simmygameapi.api.team.GameTeamManager;
import me.notsodelayed.thenexus.NexusMap;
import me.notsodelayed.thenexus.TheNexus;
import me.notsodelayed.thenexus.entity.Nexus;
import me.notsodelayed.thenexus.event.NexusDestroyedEvent;
import me.notsodelayed.thenexus.team.NexusTeam;

public abstract class NexusGame<M extends NexusMap, T extends NexusTeam> extends MapGame<M> implements TeamVsTeamGame<T> {

    private final GameTeamManager<T> teamManager;
    private final Map<T, Nexus> teamNexus = new HashMap<>();

    /**
     * Creates a MapGame without pre-defined {@link FixedMap}.
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

    public Nexus getTeamNexus(NexusTeam team) {
        if (team != getTeamAlpha() && team != getTeamBeta())
            throw new IllegalArgumentException(team.displayNameOrId() + " is not a part of " + getFormattedName());
        Nexus nexus = teamNexus.get((T) team);
        if (nexus == null)
            throw new IllegalArgumentException(team.displayNameOrId() + " in " + getFormattedName() + " does not have a nexus");
        return nexus;
    }

    public List<Nexus> getNexuses() {
        return List.copyOf(teamNexus.values());
    }

    /**
     * @param nexus the nexus
     * @return the associated team
     * @throws IllegalArgumentException if provided nexus is not registered in this game
     */
    public T getNexusTeam(Nexus nexus) {
        Optional<T> team = teamNexus.entrySet().stream()
                .filter(entry -> entry.getValue() == nexus)
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
        Bukkit.getPluginManager().registerEvents(new Listener() {
            @EventHandler
            public void nexusBreak(BlockBreakEvent event) {
                Block block = event.getBlock();
                if (!(block.getType() == Material.END_STONE))
                    return;
                // This shouldn't throw CCE
                Nexus nexus = Nexus.get(block);
                if (nexus == null)
                    return;
                event.setCancelled(true);
                GamePlayer gamePlayer = GamePlayer.get(event.getPlayer());
                if (!(gamePlayer instanceof NexusPlayer nexusPlayer))
                    return;
                NexusGame<? extends NexusMap, ? extends NexusTeam> nexusGame = nexus.getGame();
                if (!(nexusPlayer.getGame() == nexusGame))
                    return;
                if (nexusGame.getTeamNexus(nexusPlayer.getTeam()) == nexus) {
                    nexusPlayer.playSound(Sound.ENTITY_ITEM_BREAK, 1, 0);
                    nexusPlayer.message(SimmyGameAPI.mini().deserialize("<red><bold>STOP!<reset><red> This is your team's nexus!"));
                    return;
                }
                nexus.damage(nexusPlayer);
            }

            @EventHandler
            public void nexusDestroyed(NexusDestroyedEvent event) {
                NexusGame<?, ?> nexusGame = event.getGame();
                List<Nexus> aliveNexuses = nexusGame.getNexuses().stream()
                        .filter(nexus -> nexus.getHealth() > 0)
                        .toList();
                if (aliveNexuses.size() > 1)
                    return;
                if (!aliveNexuses.isEmpty()) {
                    NexusTeam winner = nexusGame.getNexusTeam(aliveNexuses.getFirst());
                    nexusGame.dispatchPrefixedMessage(winner.componentDisplayName().append(SimmyGameAPI.mini().deserialize("<green> is the winner!")));
                } else {
                    nexusGame.dispatchPrefixedMessage("Its a tie!");
                }
                // TODO override end() with integrated checks of above
                nexusGame.end();
            }
        }, TheNexus.instance);
    }

}
