package me.notsodelayed.towerdefense.game;

import java.io.File;
import java.util.Set;

import net.kyori.adventure.text.format.NamedTextColor;
import org.bukkit.GameMode;
import org.jetbrains.annotations.NotNull;

import me.notsodelayed.simmygameapi.api.game.MapGame;
import me.notsodelayed.simmygameapi.api.game.TeamVsTeamGame;
import me.notsodelayed.simmygameapi.api.map.GameMapManager;
import me.notsodelayed.simmygameapi.api.player.GamePlayer;
import me.notsodelayed.simmygameapi.api.team.GameTeam;
import me.notsodelayed.simmygameapi.api.team.GameTeamManager;
import me.notsodelayed.towerdefense.TowerDefense;

public class DefenseGame extends MapGame<DefenseMap> implements TeamVsTeamGame<GameTeam> {

    private static final GameMapManager<DefenseMap> MAP_MANAGER = new GameMapManager<>();
    private final GameTeamManager<GameTeam> teamManager = new GameTeamManager<>();
    private final GameTeam teamAlpha, teamBeta;

    public static void register() {
        // TODO proper registrations
        try {
            MAP_MANAGER.registerMap(new DefenseMap("testmap", "Test Map", new File(TowerDefense.instance.getDataFolder(), "maps/testmap")));
        } catch (Exception ex) {
            TowerDefense.logger.warning("Load in a fuking map please.");
        }
    }

    protected DefenseGame(int minPlayers, int maxPlayers) {
        super(minPlayers, maxPlayers);
        teamAlpha = new GameTeam(NamedTextColor.RED);
        teamBeta = new GameTeam(NamedTextColor.BLUE);
        getTeamManager().registerTeam(teamAlpha);
        getTeamManager().registerTeam(teamBeta);
    }

    @Override
    public Set<? extends GamePlayer> getPlayers() {
        return getPlayers(DefensePlayer.class);
    }

    @Override
    public @NotNull GameMapManager<DefenseMap> getMapManager() {
        return MAP_MANAGER;
    }

    @Override
    public @NotNull GameMode getGameMode() {
        return GameMode.SURVIVAL;
    }

    @Override
    public GameTeamManager<GameTeam> getTeamManager() {
        return teamManager;
    }

    @Override
    public GameTeam getTeamAlpha() {
        return teamAlpha;
    }

    @Override
    public GameTeam getTeamBeta() {
        return teamBeta;
    }

}
