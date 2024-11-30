package me.notsodelayed.simmygameapi.api.game.feature;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;
import java.util.function.Predicate;

import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.Sound;
import org.bukkit.block.Block;
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
import me.notsodelayed.simmygameapi.util.MessageUtil;
import me.notsodelayed.simmygameapi.util.StringUtil;

/**
 * Represents a container (e.g. Chest, Furnance) owned by a player.
 */
public class SecureContainer {

    private static final Map<Location, SecureContainer> CONTAINERS = new HashMap<>();
    private final Location location;
    private final UUID owner;
    private final Predicate<GamePlayer> openCondition;

    static {
        Bukkit.getPluginManager().registerEvents(new Listener() {

            @EventHandler
            public void chestOpenEvent(PlayerInteractEvent event) {
                if (event.getAction() != Action.RIGHT_CLICK_BLOCK)
                    return;
                assert event.getClickedBlock() != null;
                if (event.getClickedBlock().getType() != Material.CHEST)
                    return;
                SecureContainer container = CONTAINERS.get(event.getClickedBlock().getLocation());
                if (container == null)
                    return;
                Player player = event.getPlayer();
                GamePlayer gamePlayer = GamePlayer.get(player);
                if (gamePlayer == null || !gamePlayer.isOutdated())
                    return;
                if (!container.openCondition.test(gamePlayer)) {
                    event.setCancelled(true);
                    player.playSound(player.getLocation(), Sound.BLOCK_CHEST_LOCKED, 1, 2);
                    if (!container.getOwner().equals(player.getUniqueId())) {
                        player.sendMessage(StringUtil.color("&cYou cannot open this chest owned by another player!"));
                    } else {
                        player.sendMessage(StringUtil.color("&cYou cannot open your chest at this time!"));
                    }
                }
            }

        }, SimmyGameAPI.instance);
    }

    /**
     * @param container the block container
     * @param owner the block owner
     * @param openCondition the condition to use this container, or null for container owner condition
     */
    public SecureContainer(BlockInventoryHolder container, UUID owner, @Nullable Predicate<GamePlayer> openCondition) {
        this.owner = owner;
        location = container.getBlock().getLocation();
        this.openCondition = openCondition != null ? openCondition : player -> owner.equals(player.getUuid()) ;
        CONTAINERS.put(container.getBlock().getLocation(), this);
    }

    protected void unsecure() {
        CONTAINERS.remove(location);
    }

    public boolean canOpenBy(Player player) {
        return owner.equals(player.getUniqueId());
    }

    public Location getLocation() {
        return location.clone();
    }

    public UUID getOwner() {
        return owner;
    }

}
