package me.notsodelayed.thenexus.game;

import java.io.File;
import java.util.Locale;

import net.kyori.adventure.bossbar.BossBar;
import net.kyori.adventure.bossbar.BossBarViewer;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import net.kyori.adventure.text.format.TextDecoration;
import org.bukkit.GameMode;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import me.notsodelayed.simmygameapi.SimmyGameAPI;
import me.notsodelayed.simmygameapi.api.Matchmaking;
import me.notsodelayed.simmygameapi.api.feature.PlayerContainers;
import me.notsodelayed.simmygameapi.api.feature.RespawnableBlocks;
import me.notsodelayed.simmygameapi.api.map.GameMapManager;
import me.notsodelayed.thenexus.TheNexus;
import me.notsodelayed.thenexus.entity.Nexus;
import me.notsodelayed.thenexus.map.DuelNexusMap;
import me.notsodelayed.thenexus.team.NexusTeam;

public class DuelNexusGame extends NexusGame<DuelNexusMap, NexusTeam> {

    private static GameMapManager<DuelNexusMap> MAP_MANAGER;
    private static final Component PREFIX = SimmyGameAPI.miniMessage().deserialize("<dark_gray>[<gold><bold>The<yellow>Bridge<reset><dark_gray>]<white>");
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
        enableFeature(PlayerContainers.class, containers -> {
            containers.addType(Material.CHEST, Material.FURNACE, Material.SMOKER, Material.BLAST_FURNACE);
        });
        enableFeature(RespawnableBlocks.class, blocks -> {
            blocks.registerBlock(Material.COAL_ORE, Material.BEDROCK, 12);
            blocks.registerBlock(Material.IRON_ORE, Material.BEDROCK, 20);
            blocks.registerBlock(Material.GOLD_ORE, Material.BEDROCK, 25);
            blocks.registerBlock(Material.DIAMOND_ORE, Material.BEDROCK, 30);
            blocks.registerBlock(Material.REDSTONE_ORE, Material.BEDROCK, 25);
            blocks.registerBlock(Material.LAPIS_ORE, Material.BEDROCK, 25);
        });
        ready();
    }

    @Override
    protected void init() {
        super.init();
        SimmyGameAPI.scheduler().runTask(() -> {
            dispatchPrefixedMessage("init");
            Nexus nexusAlpha = new Nexus(this, getMap().getNexusAlpha().toBukkitLocation(getGameWorldName()), 10, 10, true);
            Nexus nexusBeta = new Nexus(this, getMap().getNexusBeta().toBukkitLocation(getGameWorldName()), 10, 10, true);
            registerNexus(getTeamAlpha(), nexusAlpha);
            registerNexus(getTeamBeta(), nexusBeta);
            getPlayers().forEach(player -> {
                getTeamManager().joinRandom(player);
                updateNexusBossBar(player, null);
            });
        });
    }

    private BossBar bar;
    // TODO TEMP CODE -- consider a dedicated class for managing scoreboard/bossbar?
    /* Half the code below is speedrunned and will consider a proper rewrite */
    public void updateNexusBossBar(NexusPlayer player, @Nullable NexusTeam team) {
        if (player.getPlayer() == null)
            return;
        Player bukkitPlayer = player.getPlayer();
        Nexus[] nexuses = getNexuses().toArray(Nexus[]::new);
        Component title = Component.text(nexuses[0].getHealth(), getNexusTeam(nexuses[0]).getColor(), TextDecoration.BOLD)
                .append(Component.text("  -=:=-  ", NamedTextColor.DARK_GRAY))
                .append(Component.text(nexuses[1].getHealth(), getNexusTeam(nexuses[1]).getColor(), TextDecoration.BOLD));
        float progress;
        if (nexuses[0].getHealth() == nexuses[0].getMaxHealth() && nexuses[1].getHealth() == nexuses[1].getMaxHealth()) {
            progress = 1;
        } else {
            Nexus nexus = getTeamNexus(team);
            progress = (float) nexus.getHealth() / nexus.getMaxHealth();
        }
        BossBar.Color color = team != null ? BossBar.Color.valueOf(team.getId().toUpperCase(Locale.ENGLISH)) : BossBar.Color.WHITE;
        if (bar == null) {
            bar = BossBar.bossBar(title, progress, color, BossBar.Overlay.PROGRESS);
        } else {
            bar.name(title)
                    .progress(progress)
                    .color(color);
        }
        for (BossBarViewer viewer : bar.viewers()) {
            if (viewer.equals(bukkitPlayer))
                break;
        }
        bar.addViewer(bukkitPlayer);
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
