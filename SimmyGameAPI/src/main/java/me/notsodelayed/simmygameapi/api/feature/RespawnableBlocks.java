package me.notsodelayed.simmygameapi.api.feature;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;
import java.util.WeakHashMap;
import java.util.concurrent.atomic.AtomicInteger;

import it.unimi.dsi.fastutil.Pair;
import net.kyori.adventure.text.Component;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.Tag;
import org.bukkit.block.Block;
import org.bukkit.block.data.BlockData;
import org.bukkit.entity.ArmorStand;
import org.bukkit.entity.EntityType;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.entity.CreatureSpawnEvent;
import org.jetbrains.annotations.ApiStatus;
import org.jetbrains.annotations.Nullable;

import me.notsodelayed.simmygameapi.SimmyGameAPI;
import me.notsodelayed.simmygameapi.api.Game;
import me.notsodelayed.simmygameapi.api.GameMap;
import me.notsodelayed.simmygameapi.api.GamePlayer;
import me.notsodelayed.simmygameapi.api.game.MapGame;

// TODO Test this
public class RespawnableBlocks extends Feature {

    private static final WeakHashMap<MapGame<? extends GameMap>, RespawnableBlocks> INSTANCES = new WeakHashMap<>();
    private static final Map<Material, Pair<Material, Integer>> MATERIAL_RESPAWN_DATA = new HashMap<>();
    private static final Map<Tag<Material>, Pair<Material, Integer>> TAG_RESPAWN_DATA = new HashMap<>();

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
        MATERIAL_RESPAWN_DATA.put(type, Pair.of(gracePeriodType, respawn));
    }

    /**
     * @param type the block type
     * @param gracePeriodType the block type during respawning
     * @param respawn the duration, in seconds, to respawn
     */
    public void registerBlock(Tag<Material> type, Material gracePeriodType, int respawn) {
        TAG_RESPAWN_DATA.put(type, Pair.of(gracePeriodType, respawn));
    }

    public @Nullable Pair<Material, Integer> getRespawnData(Material type) {
        Pair<Material, Integer> data;
        data = MATERIAL_RESPAWN_DATA.get(type);
        if (data == null) {
            Optional<Pair<Material, Integer>> queriedData = TAG_RESPAWN_DATA.entrySet().stream()
                    .filter(entry -> entry.getKey().isTagged(type))
                    .map(Map.Entry::getValue)
                    .findAny();
            data = queriedData.orElse(null);
        }
        return data;
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
                Pair<Material, Integer> respawnData = instance.getRespawnData(block.getType());
                if (respawnData == null)
                    return;
                Material currentType = block.getType();
                BlockData currentBlockData = block.getBlockData();
                SimmyGameAPI.scheduler().runTask(() -> {
                    block.setType(respawnData.left());
                    ArmorStand holo = (ArmorStand) mapGame.getWorld().spawnEntity(block.getLocation().toCenterLocation().add(0, -0.25, 0), EntityType.ARMOR_STAND, CreatureSpawnEvent.SpawnReason.CUSTOM, entity -> {
                        ArmorStand stand = (ArmorStand) entity;
                        stand.setSmall(true);
                        stand.setMarker(true);
                        stand.setCanMove(false);
                        stand.setCustomNameVisible(true);
                        stand.setVisible(false);
                    });
                    AtomicInteger seconds = new AtomicInteger(respawnData.right());
                    SimmyGameAPI.scheduler().runTaskTimer(task -> {
                        holo.customName(Component.text(seconds.get()));
                        if (seconds.getAndDecrement() == 0) {
                            holo.remove();
                            // Fixes currentType bound to event.getBlock()
                            Block newBlock = block.getLocation().getBlock();
                            newBlock.setType(currentType);
                            newBlock.setBlockData(currentBlockData);
                            task.cancel();
                        }
                    }, 0, 20);
                });
            }
        }, SimmyGameAPI.instance);
    }

    @ApiStatus.Internal
    public static void init() {
        registerCreator(RespawnableBlocks.class, RespawnableBlocks::new);
    }

}
