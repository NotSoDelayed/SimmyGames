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

// TODO Test this
public class RespawnableBlocks extends Feature {

    private static final WeakHashMap<MapGame, RespawnableBlocks> INSTANCES = new WeakHashMap<>();

    private record RespawnData(Material gracePeriodType, int respawnDelay) {}

    private final Map<Material, RespawnData> respawnData = new HashMap<>();

    private RespawnableBlocks(MapGame game) {
        super(game);
        if (INSTANCES.containsKey(game))
            throw new IllegalStateException("attempted to reinitialise RespawnableBlocks for game " + game.getFormattedName());
        INSTANCES.put(game, this);
    }

    public void registerBlock(Material type, Material gracePeriodType, int respawn) {
        respawnData.put(type, new RespawnData(gracePeriodType, respawn));
    }

    static {
        Bukkit.getPluginManager().registerEvents(new Listener() {
            @EventHandler
            public void blockBreak(BlockBreakEvent event) {
                GamePlayer gamePlayer = GamePlayer.get(event.getPlayer());
                if (gamePlayer == null)
                    return;
                Game game = gamePlayer.getGame();
                if (!(game instanceof MapGame mapGame))
                    return;
                RespawnableBlocks manager = INSTANCES.get(mapGame);
                if (manager == null)
                    return;
                Block block = event.getBlock();
                RespawnData respawn = manager.respawnData.get(block.getType());
                if (respawn == null)
                    return;
                Material currentType = block.getType();
                block.setType(respawn.gracePeriodType);
                ArmorStand holo = (ArmorStand) mapGame.getWorld().spawnEntity(block.getLocation().add(0, -2, 0), EntityType.ARMOR_STAND, CreatureSpawnEvent.SpawnReason.CUSTOM, entity -> {
                    ArmorStand stand = (ArmorStand) entity;
                    stand.setSmall(true);
                    stand.setMarker(true);
                    stand.setCanMove(false);
                    stand.setVisible(false);
                });
                AtomicInteger seconds = new AtomicInteger(respawn.respawnDelay);
                SimmyGameAPI.scheduler().runTaskTimer(task -> {
                    if (seconds.get() == 0) {
                        holo.remove();
                        block.setType(currentType);
                        task.cancel();
                        return;
                    }
                    holo.customName(Component.text(seconds.getAndDecrement()));
                }, 0, 20);
            }
        }, SimmyGameAPI.instance);
    }

    @ApiStatus.Internal
    public static void init() {
        registerCreator(RespawnableBlocks.class, RespawnableBlocks::new);
    }

}
