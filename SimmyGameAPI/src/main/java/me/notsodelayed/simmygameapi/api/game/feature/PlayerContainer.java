package me.notsodelayed.simmygameapi.api.game.feature;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;
import java.util.function.Predicate;

import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.Sound;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.block.BlockPlaceEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.BlockInventoryHolder;
import org.jetbrains.annotations.Nullable;

import me.notsodelayed.simmygameapi.SimmyGameAPI;
import me.notsodelayed.simmygameapi.api.game.player.GamePlayer;
import me.notsodelayed.simmygameapi.util.CompareUtil;
import me.notsodelayed.simmygameapi.util.StringUtil;

/**
 * Represents a container (e.g. Chest, Furnance) owned by a player.
 */
public class PlayerContainer {

    private static final Map<Location, PlayerContainer> CONTAINERS = new HashMap<>();
    private final Location location;
    private final UUID owner;
    private final Predicate<GamePlayer> openCondition;
    private static final Predicate<Material> IS_CONTAINER_TYPE = material -> CompareUtil.equalsAny(material, Material.CHEST, Material.FURNACE, Material.BLAST_FURNACE);

    static {
        Bukkit.getPluginManager().registerEvents(new Listener() {

            @EventHandler
            public void containerPlaceEvent(BlockPlaceEvent event) {
                if (!IS_CONTAINER_TYPE.test(event.getBlock().getType()))
                    return;
                Player player = event.getPlayer();
                player.playSound(player.getLocation(), Sound.BLOCK_CHEST_LOCKED, 1, 2);
                player.sendActionBar(StringUtil.color("&bYou have placed a personal container!"));
            }

            @EventHandler
            public void containerOpenEvent(PlayerInteractEvent event) {
                if (event.getAction() != Action.RIGHT_CLICK_BLOCK || event.getClickedBlock() == null)
                    return;
                if (!IS_CONTAINER_TYPE.test(event.getClickedBlock().getType()))
                    return;
                PlayerContainer container = CONTAINERS.get(event.getClickedBlock().getLocation());
                if (container == null)
                    return;
                Player player = event.getPlayer();
                GamePlayer gamePlayer = GamePlayer.get(player);
                if (gamePlayer == null || !gamePlayer.isOutdated())
                    return;
                if (!container.canUse(gamePlayer)) {
                    event.setCancelled(true);
                    player.playSound(player.getLocation(), Sound.BLOCK_CHEST_LOCKED, 1, 2);
                    player.sendMessage(StringUtil.color("&cYou cannot open this container owned by another player!"));
                }
            }

        }, SimmyGameAPI.instance);
    }

    /**
     * @param container the block container
     * @param owner the block owner
     * @param openCondition the condition to use this container for other players, or null for only owner to use
     */
    public PlayerContainer(BlockInventoryHolder container, UUID owner, @Nullable Predicate<GamePlayer> openCondition) {
        this.owner = owner;
        location = container.getBlock().getLocation();
        this.openCondition = openCondition != null ? openCondition : player -> false;
        CONTAINERS.put(container.getBlock().getLocation(), this);
    }

    /**
     * Unsecures this container.
     * @apiNote This instance will be useless after.
     */
    protected void unsecure() {
        CONTAINERS.remove(location);
    }

    /**
     * @param player the user
     * @return whether the user can open this container
     * @apiNote This will always return <b>true</b> for container owner.
     */
    public boolean canUse(GamePlayer player) {
        return owner.equals(player.getUuid()) || openCondition.test(player);
    }

    public Location getLocation() {
        return location.clone();
    }

    public UUID getOwner() {
        return owner;
    }

    public boolean isValid() {
        return CONTAINERS.containsKey(location);
    }

}
