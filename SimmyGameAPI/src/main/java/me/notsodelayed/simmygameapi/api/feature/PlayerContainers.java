package me.notsodelayed.simmygameapi.api.feature;

import java.util.*;
import java.util.function.Predicate;

import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.Sound;
import org.bukkit.block.Block;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.block.BlockPlaceEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.BlockInventoryHolder;
import org.jetbrains.annotations.ApiStatus;
import org.jetbrains.annotations.Nullable;

import me.notsodelayed.simmygameapi.SimmyGameAPI;
import me.notsodelayed.simmygameapi.api.BasePlayer;
import me.notsodelayed.simmygameapi.api.Game;
import me.notsodelayed.simmygameapi.api.GamePlayer;
import me.notsodelayed.simmygameapi.api.game.MapGame;
import me.notsodelayed.simmygameapi.api.player.TeamPlayer;
import me.notsodelayed.simmygameapi.util.ComponentUtil;

// TODO TEST THIS
public class PlayerContainers extends Feature {

    private static final WeakHashMap<MapGame<?>, PlayerContainers> INSTANCES = new WeakHashMap<>();

    private final Set<Material> containerTypes = new HashSet<>();

    private PlayerContainers(MapGame<?> game) {
        super(game);
        INSTANCES.put(game, this);
    }

    /**
     * @param container the block container
     * @param owner the container owner
     * @param openCondition the condition for other players to use this container, otherwise null
     * @apiNote This method will silently fail if the provided block does not match with any of {@link #getTypes()}
     */
    public void secure(Block container, BasePlayer owner, @Nullable Predicate<GamePlayer> openCondition) {
        if (!supportType(container.getType()))
            return;
        new PlayerContainer(container, owner.getUuid(), openCondition);
    }

    public boolean supportType(Material type) {
        return containerTypes.contains(type);
    }

    public Set<Material> getTypes() {
        return Set.copyOf(containerTypes);
    }

    /**
     * @param types the types of {@link BlockInventoryHolder}
     */
    public void addType(Material... types) {
        for (Material type : types) {
            if (!(type.createBlockData().createBlockState() instanceof BlockInventoryHolder))
                throw new IllegalStateException("provided type " + type + " is not a BlockInventoryHolder type");
            containerTypes.add(type);
        }
    }

    static {
        Bukkit.getPluginManager().registerEvents(new Listener() {
            @EventHandler
            public void containerPlaceEvent(BlockPlaceEvent event) {
                // TODO put this in helper lambda
                GamePlayer gamePlayer = GamePlayer.get(event.getPlayer());
                if (gamePlayer == null)
                    return;
                Game game = gamePlayer.getGame();
                if (!(game instanceof MapGame<?> mapGame))
                    return;
                if (!mapGame.hasFeature(PlayerContainers.class))
                    return;
                PlayerContainers instance = INSTANCES.get(mapGame);
                if (instance == null) {
                    SimmyGameAPI.logger.severe(mapGame.getFormattedName() + " has feature 'PlayerContainers' but does not have its instance.");
                    throw new IllegalStateException("registered feature without its instance");
                }
                if (!instance.supportType(event.getBlock().getType()))
                    return;
                // TODO detach teamplayer from this
                if (!(gamePlayer instanceof TeamPlayer<?> teamPlayer))
                    return;
                Location location = event.getBlock().getLocation();
                instance.secure(event.getBlock(), teamPlayer, user -> {
                    if (!(user instanceof TeamPlayer<?> userTeamPlayer))
                        return false;
                    return teamPlayer.getTeam() != userTeamPlayer.getTeam();
                });
                gamePlayer.playSound(location, Sound.BLOCK_CHEST_LOCKED, 1, 2);
                gamePlayer.actionbar(SimmyGameAPI.miniMessage().deserialize("<gold>You have placed a personal container!"));
            }

            @EventHandler
            public void containerOpenEvent(PlayerInteractEvent event) {
                if (event.getAction() != Action.RIGHT_CLICK_BLOCK)
                    return;
                if (event.getClickedBlock() == null)
                    return;
                GamePlayer gamePlayer = GamePlayer.get(event.getPlayer());
                if (gamePlayer == null)
                    return;
                Game game = gamePlayer.getGame();
                if (!(game instanceof MapGame<?> mapGame))
                    return;
                if (!mapGame.hasFeature(PlayerContainers.class))
                    return;
                PlayerContainers instance = INSTANCES.get(mapGame);
                if (instance == null) {
                    SimmyGameAPI.logger.severe(mapGame.getFormattedName() + " has feature 'PlayerContainers' but does not have its instance.");
                    throw new IllegalStateException("registered feature without its instance");
                }
                if (!instance.supportType(event.getClickedBlock().getType()))
                    return;
                // Stop the container from being accessed
                event.setCancelled(true);
                PlayerContainer container = PlayerContainer.CONTAINERS.get(event.getClickedBlock().getLocation());
                GamePlayer userPlayer = GamePlayer.get(event.getPlayer());
                if (userPlayer == null) {
                    event.getPlayer().sendMessage(ComponentUtil.errorMessage("You are not a part of this game to use this."));
                    return;
                }
                if (!container.canUse(userPlayer)) {
                    userPlayer.playSound(Sound.BLOCK_CHEST_LOCKED, 1, 2);
                    userPlayer.message(SimmyGameAPI.miniMessage().deserialize("<red>You cannot open this container owned by another player!"));
                    return;
                }
                event.setCancelled(false);
            }

        }, SimmyGameAPI.instance);
    }

    private static class PlayerContainer {

        private static final Map<Location, PlayerContainer> CONTAINERS = new HashMap<>();
        private final Location location;
        private final UUID owner;
        private final Predicate<GamePlayer> openCondition;

        private PlayerContainer(Block container, UUID owner, @Nullable Predicate<GamePlayer> openCondition) {
            this.owner = owner;
            location = container.getLocation();
            this.openCondition = openCondition != null ? openCondition : player -> false;
            CONTAINERS.put(container.getLocation(), this);
        }

        /**
         * Unsecures this container.
         * @apiNote This instance will be useless after.
         */
        private void unsecure() {
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

    @ApiStatus.Internal
    public static void init() {
        registerCreator(PlayerContainers.class, PlayerContainers::new);
    }

}
