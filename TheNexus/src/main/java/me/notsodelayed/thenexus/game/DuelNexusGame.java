package me.notsodelayed.thenexus.game;

import java.io.File;
import java.util.concurrent.CompletableFuture;

import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import org.bukkit.GameMode;
import org.jetbrains.annotations.NotNull;

import me.notsodelayed.simmygameapi.SimmyGameAPI;
import me.notsodelayed.simmygameapi.api.Matchmaking;
import me.notsodelayed.simmygameapi.api.game.TeamGame;
import me.notsodelayed.simmygameapi.api.map.GameMapManager;
import me.notsodelayed.thenexus.TheNexus;
import me.notsodelayed.thenexus.entity.Nexus;
import me.notsodelayed.thenexus.map.DuelNexusMap;
import me.notsodelayed.thenexus.team.NexusTeam;

public class DuelNexusGame extends NexusGame<DuelNexusMap, NexusTeam> {

    private static GameMapManager<DuelNexusMap> MAP_MANAGER;
    private static final Component PREFIX = SimmyGameAPI.miniMessage().deserialize("<dark_gray>[<gold><bold>The<yellow>Bridge<dark_gray>]<white>");
    private final NexusTeam teamAlpha, teamBeta;

    // TODO might need a better way to register maps across sub game systems
    public static void register() {
        MAP_MANAGER = new GameMapManager<>();
        MAP_MANAGER.registerMap(new DuelNexusMap("testmap", new File(TheNexus.instance.getDataFolder(), "maps/testmap")));
        Matchmaking.registerGameCreator(DuelNexusGame.class, () -> new DuelNexusGame(1, 10));
        Matchmaking.registerGame(DuelNexusGame.class, NexusPlayer::new);
    }

    public DuelNexusGame(int minPlayers, int maxPlayers) {
        super(minPlayers, maxPlayers);
        teamAlpha = new NexusTeam(NamedTextColor.RED);
        teamBeta = new NexusTeam(NamedTextColor.BLUE);
        getTeamManager().registerTeam(teamAlpha);
        getTeamManager().registerTeam(teamBeta);
        ready();
    }

    @Override
    public void init() {
        // TODO find a way to improve this -- adapt CompletableFuture??
        CompletableFuture.runAsync(() -> SimmyGameAPI.scheduler().runTask(super::init))
                .thenRun(() -> SimmyGameAPI.scheduler().runTask(() -> {
                    dispatchPrefixedMessage("initt");
                    getPlayers().forEach(player -> getTeamManager().joinRandom(player));
                    Nexus nexusAlpha = new Nexus(this, getMap().getNexusAlpha().toBukkitLocation(getWorld()), 25, 25, true);
                    Nexus nexusBeta = new Nexus(this, getMap().getNexusBeta().toBukkitLocation(getWorld()), 25, 25, true);
                    registerNexus(getTeamAlpha(), nexusAlpha);
                    registerNexus(getTeamAlpha(), nexusBeta);
                }));
    }

    @Override
    public @NotNull Component getPrefix() {
        return PREFIX;
    }

    @Override
    public @NotNull GameMode getGameMode() {
        return GameMode.SURVIVAL;
    }

    @Override
    public @NotNull GameMapManager<DuelNexusMap> getMapManager() {
        return MAP_MANAGER;
    }

    @Override
    public NexusTeam getTeamAlpha() {
        return teamAlpha;
    }

    @Override
    public NexusTeam getTeamBeta() {
        return teamBeta;
    }

}
