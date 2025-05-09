package me.notsodelayed.simmygameapi.api.feature;

import java.util.*;

import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.Tag;
import org.bukkit.block.Block;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.ApiStatus;

import me.notsodelayed.simmygameapi.SimmyGameAPI;
import me.notsodelayed.simmygameapi.api.Game;
import me.notsodelayed.simmygameapi.api.GamePlayer;
import me.notsodelayed.simmygameapi.api.game.MapGame;

public class Telekinesis extends Feature {

    private static final WeakHashMap<MapGame<?>, Telekinesis> INSTANCES = new WeakHashMap<>();
    private final Set<Material> materialTypes = new HashSet<>();
    private final Set<Tag<Material>> tagTypes = new HashSet<>();
    private static boolean isAll = false;

    private Telekinesis(MapGame<?> game) {
        super(game);
        INSTANCES.put(game, this);
    }

    /**
     * Enables Telekinesis effect on all blocks.
     */
    public void all() {
        isAll = true;
    }

    public void registerType(Material... types) {
        materialTypes.addAll(Arrays.asList(types));
    }

    @SafeVarargs
    public final void registerType(Tag<Material>... types) {
        tagTypes.addAll(Arrays.asList(types));
    }

    public boolean isRegisteredType(Material type) {
        if (materialTypes.contains(type))
            return true;
        return tagTypes.stream().anyMatch(tag -> tag.isTagged(type));
    }

    static {
        Bukkit.getPluginManager().registerEvents(new Listener() {
            @EventHandler(priority = EventPriority.LOW, ignoreCancelled = true)
            public void blockBreak(BlockBreakEvent event) {
                Player player = event.getPlayer();
                GamePlayer gamePlayer = GamePlayer.get(player);
                if (gamePlayer == null)
                    return;
                Game game = gamePlayer.getGame();
                if (!(game instanceof MapGame<?> mapGame))
                    return;
                if (!mapGame.hasFeature(RespawnableBlocks.class))
                    return;
                Telekinesis instance = INSTANCES.get(mapGame);
                if (instance == null) {
                    SimmyGameAPI.logger.severe(mapGame.getFormattedName() + " has feature 'Telekinesis' but does not have its instance.");
                    throw new IllegalStateException("registered feature without its instance");
                }
                Block block = event.getBlock();
                if (!isAll && !instance.isRegisteredType(block.getType()))
                    return;
                int exp = event.getExpToDrop();
                event.setExpToDrop(0);
                Collection<ItemStack> drops = block.getDrops(player.getInventory().getItemInMainHand(), player);
                event.setDropItems(false);
                player.giveExp(exp);
                Map<Integer, ItemStack> leftovers = player.getInventory().addItem(drops.toArray(ItemStack[]::new));
                if (!leftovers.isEmpty())
                    leftovers.values().forEach(item -> player.getWorld().dropItemNaturally(event.getBlock().getLocation(), item));
            }
        }, SimmyGameAPI.instance);
    }

    @ApiStatus.Internal
    public static void init() {
        Feature.registerCreator(Telekinesis.class, Telekinesis::new);
    }

}
