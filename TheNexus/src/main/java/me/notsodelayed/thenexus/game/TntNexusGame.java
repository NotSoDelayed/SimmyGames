package me.notsodelayed.thenexus.game;

import java.io.File;
import java.util.WeakHashMap;

import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import net.kyori.adventure.text.format.TextDecoration;
import org.bukkit.Bukkit;
import org.bukkit.GameMode;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.NotNull;

import me.notsodelayed.simmygameapi.api.Matchmaking;
import me.notsodelayed.simmygameapi.api.game.map.GameMapManager;
import me.notsodelayed.simmygameapi.util.Util;
import me.notsodelayed.thenexus.TheNexus;
import me.notsodelayed.thenexus.entity.NexusPlayer;
import me.notsodelayed.thenexus.entity.team.TntNexusTeam;
import me.notsodelayed.thenexus.map.TntNexusMap;

public class TntNexusGame extends NexusGame<TntNexusMap, TntNexusTeam> {

    private static GameMapManager<TntNexusMap> MAP_MANAGER;

    private final WeakHashMap<Player, Inventory> virtualChest = new WeakHashMap<>();

    public static void register() {
        TheNexus.logger.info("Registering maps...");
        MAP_MANAGER = new GameMapManager<>();
        for (File mapDirectory : new File(TheNexus.instance.getDataFolder(), "maps").listFiles()) {
            try {
                if (!mapDirectory.isDirectory())
                    continue;
                MAP_MANAGER.registerMap(new TntNexusMap(mapDirectory.getName(), mapDirectory));
            } catch (Exception ignored) {
                TheNexus.logger.warning("Skipping " + mapDirectory.getName() + " due to exception occurred...");
            }
        }
        TheNexus.logger.info("Successfully registered " + MAP_MANAGER.size() + " maps!");

        TheNexus.logger.info("Registering game into matchmaking...");
        Matchmaking.registerGame(TntNexusGame.class, NexusPlayer::new);
    }

    protected TntNexusGame(int minPlayers, int maxPlayers) {
        super(minPlayers, maxPlayers);
        TntNexusTeam teamAlpha = new TntNexusTeam(NamedTextColor.RED);
        TntNexusTeam teamBeta = new TntNexusTeam(NamedTextColor.BLUE);
        setTeamAlpha(teamAlpha);
        setTeamBeta(teamBeta);
    }

    @Override
    public @NotNull GameMapManager<TntNexusMap> getMapManager() {
        return MAP_MANAGER;
    }

    public void wipeVirtualChests() {
        virtualChest.clear();
    }

    public @NotNull Inventory getVirtualChest(Player player) {
        return virtualChest.computeIfAbsent(player, p -> {
            Inventory inv = Bukkit.createInventory(null, 36, Component.text("Virtual Chest").color(NamedTextColor.YELLOW).decorate(TextDecoration.BOLD));

            Util.setItems(inv, new ItemStack(Material.COBBLESTONE, 64), 0, 1);
            Util.setItems(inv, new ItemStack(Material.STONE_SLAB, 64), 2, 3);
            inv.setItem(4, new ItemStack(Material.NETHERITE_PICKAXE));
            inv.setItem(5, new ItemStack(Material.FURNACE, 64));
            inv.setItem(6, new ItemStack(Material.OBSERVER, 64));
            inv.setItem(7, new ItemStack(Material.PISTON, 64));
            inv.setItem(8, new ItemStack(Material.STICKY_PISTON, 64));

            Util.setItems(inv, new ItemStack(Material.COBBLESTONE, 64), 9, 10);
            inv.setItem(11, new ItemStack(Material.COBBLESTONE_WALL, 64));
            inv.setItem(12, new ItemStack(Material.STONE_PRESSURE_PLATE, 64));
            inv.setItem(13, new ItemStack(Material.NETHERITE_PICKAXE));
            inv.setItem(14, new ItemStack(Material.DISPENSER, 64));
            inv.setItem(15, new ItemStack(Material.TARGET, 64));
            inv.setItem(16, new ItemStack(Material.STONE_BUTTON, 64));
            inv.setItem(17, new ItemStack(Material.LEVER, 64));

            inv.setItem(18, new ItemStack(Material.REDSTONE, 64));
            inv.setItem(19, new ItemStack(Material.REPEATER, 64));
            inv.setItem(20, new ItemStack(Material.COMPARATOR, 64));
            inv.setItem(21, new ItemStack(Material.SLIME_BLOCK, 64));
            inv.setItem(22, new ItemStack(Material.HONEY_BLOCK, 64));
            inv.setItem(23, new ItemStack(Material.TRIPWIRE_HOOK, 64));
            inv.setItem(24, new ItemStack(Material.STRING, 64));
            inv.setItem(25, new ItemStack(Material.OAK_PRESSURE_PLATE, 64));
            inv.setItem(26, new ItemStack(Material.HOPPER, 64));
            Util.setItems(inv, new ItemStack(Material.TNT, 64), 27, 28, 29, 30, 31, 32, 33, 34, 35);

            return inv;
        });
    }

    @Override
    public @NotNull GameMode getGameMode() {
        return GameMode.SURVIVAL;
    }

}
