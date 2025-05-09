package me.notsodelayed.thenexus.game.duelnexusgame;

import java.io.File;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;

import net.kyori.adventure.bossbar.BossBar;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import org.bukkit.Bukkit;
import org.bukkit.GameMode;
import org.bukkit.Material;
import org.bukkit.Tag;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.jetbrains.annotations.NotNull;

import me.notsodelayed.simmygameapi.SimmyGameAPI;
import me.notsodelayed.simmygameapi.api.GamePlayer;
import me.notsodelayed.simmygameapi.api.Matchmaking;
import me.notsodelayed.simmygameapi.api.feature.PlayerContainers;
import me.notsodelayed.simmygameapi.api.feature.RespawnableBlocks;
import me.notsodelayed.simmygameapi.api.feature.Telekinesis;
import me.notsodelayed.simmygameapi.api.map.GameMapManager;
import me.notsodelayed.thenexus.TheNexus;
import me.notsodelayed.thenexus.entity.Nexus;
import me.notsodelayed.thenexus.event.NexusDamagedEvent;
import me.notsodelayed.thenexus.game.NexusGame;
import me.notsodelayed.thenexus.game.NexusPlayer;
import me.notsodelayed.thenexus.team.NexusTeam;

public class DuelNexusGame extends NexusGame<DuelNexusMap, NexusTeam> {

    private static GameMapManager<DuelNexusMap> MAP_MANAGER;
    private static final Component PREFIX = SimmyGameAPI.mini().deserialize("<dark_gray>[<gold><bold>The<yellow>Bridge<reset><dark_gray>]<white>");
    private final NexusTeam teamAlpha, teamBeta;

    // TODO properly implement map registration
    public static void register() {
        MAP_MANAGER = new GameMapManager<>("DuelNexusGame");
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
        enableFeature(PlayerContainers.class, containers -> {
            containers.addType(Material.CHEST, Material.FURNACE, Material.SMOKER, Material.BLAST_FURNACE);
        });
        enableFeature(RespawnableBlocks.class, blocks -> {
            blocks.registerBlock(Tag.LOGS, Material.AIR, 15);
            blocks.registerBlock(Tag.COAL_ORES, Material.STONE, 15);
            blocks.registerBlock(Tag.IRON_ORES, Material.STONE, 20);
            blocks.registerBlock(Tag.GOLD_ORES, Material.STONE, 25);
            blocks.registerBlock(Tag.DIAMOND_ORES, Material.STONE, 30);
            blocks.registerBlock(Tag.REDSTONE_ORES, Material.STONE, 25);
            blocks.registerBlock(Tag.LAPIS_ORES, Material.STONE, 25);
        });
        enableFeature(Telekinesis.class, telekinesis -> {
            telekinesis.registerType(Tag.LOGS, Tag.COAL_ORES, Tag.IRON_ORES, Tag.GOLD_ORES, Tag.DIAMOND_ORES, Tag.REDSTONE_ORES, Tag.LAPIS_ORES);
        });
        ready();
    }

    @Override
    protected void init() {
        super.init();
        Nexus nexusAlpha = new Nexus(this, getMap().getNexusAlpha().toBukkitLocation(getWorld()), 10, 10, true);
        Nexus nexusBeta = new Nexus(this, getMap().getNexusBeta().toBukkitLocation(getWorld()), 10, 10, true);
        registerNexus(getTeamAlpha(), nexusAlpha);
        registerNexus(getTeamBeta(), nexusBeta);
        getPlayers().forEach(player -> {
            getTeamManager().joinRandom(player);
            updateBossBar(player);
        });
    }

    @Override
    public void onPlayerJoin(GamePlayer player) {
        super.onPlayerJoin(player);
        getTeamManager().joinRandom(player);
    }

    @Override
    public void onPlayerLeave(GamePlayer player) {
        super.onPlayerLeave(player);
        bossBars.values().forEach(bossbar -> bossbar.removeViewer(player.asBukkitPlayer()));
    }

    private final Map<NexusTeam, BossBar> bossBars = new HashMap<>();
    public void updateBossBar(NexusPlayer player) {
        if (bossBars.isEmpty()) {
            getTeamManager().getTeams().forEach(team -> {
                bossBars.put(team, BossBar.bossBar(Component.empty(), 0, BossBar.Color.WHITE, BossBar.Overlay.PROGRESS));
            });
        }
        bossBars.forEach((team, bossBar) -> {
            Nexus nexus = getTeamNexus(team);
            bossBar.name(team.componentDisplayName())
                    .progress((float) nexus.getHealth() / nexus.getMaxHealth())
                    .color(BossBar.Color.valueOf(team.id().toUpperCase(Locale.ENGLISH))).addViewer(player.asBukkitPlayer());
        });
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
    static {
        Bukkit.getPluginManager().registerEvents(new Listener() {
            @EventHandler
            public void nexusDamage(NexusDamagedEvent event) {
                DuelNexusGame game = (DuelNexusGame) event.getGame();
                game.getPlayers().forEach(game::updateBossBar);
            }
        }, SimmyGameAPI.instance);
    }

}
