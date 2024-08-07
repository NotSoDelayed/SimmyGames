package me.notsodelayed.thenexus.game;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;
import java.util.Set;
import java.util.TreeSet;

import com.google.common.base.Preconditions;
import me.notsodelayed.simmygameapi.api.game.GameState;
import me.notsodelayed.simmygameapi.api.game.KitGame;
import me.notsodelayed.simmygameapi.api.game.MapGame;
import me.notsodelayed.simmygameapi.api.game.TeamVsTeamGame;
import me.notsodelayed.simmygameapi.api.game.map.GameMap;
import me.notsodelayed.simmygameapi.api.game.team.GameTeamManager;
import me.notsodelayed.simmygameapi.util.LoggerUtil;
import me.notsodelayed.thenexus.entity.NexusPlayer;
import me.notsodelayed.thenexus.entity.game.Nexus;
import me.notsodelayed.thenexus.entity.team.NexusTeam;
import me.notsodelayed.thenexus.kit.NexusKit;
import me.notsodelayed.thenexus.map.NexusMap;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.jetbrains.annotations.ApiStatus;
import org.jetbrains.annotations.NotNull;

public class NexusGame extends MapGame<NexusMap> implements KitGame<NexusKit>, TeamVsTeamGame<NexusTeam> {

    // TODO for testing
    private static NexusKit WARRIOR;
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
        // TODO for testing
        if (WARRIOR == null) {
            WARRIOR = new NexusKit("warrior", Material.STONE_SWORD, new String[]{"Classic warrior kit", "Reminder to remove this static field in NexusGame.class"});
            WARRIOR.setItem(0, Material.STONE_SWORD)
                    .setItem(1,Material.WOODEN_PICKAXE)
                    .setItem(2, Material.STONE_AXE)
                    .setItem(3, Material.STONE_SHOVEL)
                    .setItem(4, Material.SHEARS)
                    .setItem(5, Material.CRAFTING_TABLE);
        }
        teamManager = new GameTeamManager<>();
        teamNexusMap = new HashMap<>();
    }

    @Override
    protected boolean init() {
        return true;
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
    public Set<NexusPlayer> getPlayers() {
        return getPlayers(NexusPlayer.class);
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
        // TODO make nexus worky
        return null;
    }



    @Override
    public GameTeamManager<NexusTeam> getTeamManager() {
        return teamManager;
    }

    @Override
    public TreeSet<NexusKit> getKits() {
        return new TreeSet<>(Set.of(WARRIOR));
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

    // TODO deprecate this shit
    /**
     * @return the kit types of this game mode, usually used for kit registrations
     */
    @ApiStatus.Internal
    public static String[] getKitTypes() {
        return KIT_TYPES.clone();
    }

}
