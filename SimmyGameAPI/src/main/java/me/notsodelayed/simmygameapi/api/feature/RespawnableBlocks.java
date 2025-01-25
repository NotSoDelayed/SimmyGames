package me.notsodelayed.simmygameapi.api.feature;

import java.util.HashMap;
import java.util.Map;
import java.util.WeakHashMap;
import java.util.concurrent.atomic.AtomicInteger;

import net.kyori.adventure.text.Component;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.entity.ArmorStand;
import org.bukkit.entity.EntityType;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.entity.CreatureSpawnEvent;
import org.jetbrains.annotations.ApiStatus;

import me.notsodelayed.simmygameapi.SimmyGameAPI;
import me.notsodelayed.simmygameapi.api.Game;
import me.notsodelayed.simmygameapi.api.GamePlayer;
import me.notsodelayed.simmygameapi.api.game.MapGame;
import me.notsodelayed.simmygameapi.api.map.GameMap;

// TODO Test this
public class RespawnableBlocks extends Feature {

    private static final WeakHashMap<MapGame<? extends GameMap> , RespawnableBlocks> INSTANCES = new WeakHashMap<>();

    private record BlockRespawnData(Material gracePeriodType, int respawnDelay) {}

    private final Map<Material, BlockRespawnData> blockRespawnData = new HashMap<>();

    private RespawnableBlocks(MapGame<? extends GameMap> game) {
        super(game);
        if (INSTANCES.containsKey(game))
            throw new IllegalStateException("attempted to reinitialise RespawnableBlocks for game " + game.getFormattedName());
        INSTANCES.put(game, this);
    }

    /**
     * @param type the block type
     * @param gracePeriodType the block type during respawning
     * @param respawn the duration, in seconds, to respawn
     */
    public void registerBlock(Material type, Material gracePeriodType, int respawn) {
        if (!type.isBlock())
            throw new IllegalArgumentException(type + " is not a block type");
        blockRespawnData.put(type, new BlockRespawnData(gracePeriodType, respawn));
    }

    static {
        Bukkit.getPluginManager().registerEvents(new Listener() {
            @EventHandler
            public void blockBreak(BlockBreakEvent event) {
                GamePlayer gamePlayer = GamePlayer.get(event.getPlayer());
                if (gamePlayer == null)
                    return;
                Game game = gamePlayer.getGame();
                if (!(game instanceof MapGame<? extends GameMap> mapGame))
                    return;
                if (!mapGame.hasFeature(RespawnableBlocks.class))
                    return;
                RespawnableBlocks instance = INSTANCES.get(mapGame);
                if (instance == null) {
                    SimmyGameAPI.logger.severe(mapGame.getFormattedName() + " has feature 'RespawnableBlocks' but does not have its instance.");
                    throw new IllegalStateException("registered feature without its instance");
                }
                Block block = event.getBlock();
                BlockRespawnData respawn = instance.blockRespawnData.get(block.getType());
                if (respawn == null)
                    return;
                Material currentType = block.getType();
                block.setType(respawn.gracePeriodType);
                ArmorStand holo = (ArmorStand) mapGame.getWorld().spawnEntity(block.getLocation().add(0, -2, 0), EntityType.ARMOR_STAND, CreatureSpawnEvent.SpawnReason.CUSTOM, entity -> {
                    ArmorStand stand = (ArmorStand) entity;
                    stand.setSmall(true);
                    stand.setMarker(true);
                    stand.setCanMove(false);
                    stand.setCustomNameVisible(true);
                    stand.setVisible(false);
                });
                AtomicInteger seconds = new AtomicInteger(respawn.respawnDelay);
                SimmyGameAPI.scheduler().runTaskTimer(task -> {
                    holo.customName(Component.text(seconds.getAndDecrement()));
                    if (seconds.getAndDecrement() == 0) {
                        holo.remove();
                        block.setType(currentType);
                        task.cancel();
                    }
                }, 0, 20);
            }
        }, SimmyGameAPI.instance);
    }

    @ApiStatus.Internal
    public static void init() {
        registerCreator(RespawnableBlocks.class, RespawnableBlocks::new);
    }

}
