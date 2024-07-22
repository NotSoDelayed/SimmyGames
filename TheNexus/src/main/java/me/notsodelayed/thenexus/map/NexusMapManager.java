package me.notsodelayed.thenexus.map;

import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;

import me.notsodelayed.simmygameapi.api.map.GameMap;
import me.notsodelayed.simmygameapi.api.map.MapChoice;
import me.notsodelayed.simmygameapi.util.Util;
import me.notsodelayed.thenexus.TheNexus;
import org.bukkit.Bukkit;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

// TODO rewrite this nigg
public class NexusMapManager {

    private static NexusMapManager mapManager;

    private final Map<String, NexusGameMap> NEXUS_MAPS = new HashMap<>();
    /**
     * The directory where it houses all {@link NexusGameMap}.
     */
    public static final File MAPS_DIRECTORY = new File(TheNexus.instance.getDataFolder(), "maps");

    private NexusMapManager(TheNexus ignored) {}

    private static boolean init(TheNexus plugin) {
        NexusMapManager current = mapManager;
        try {
            mapManager = new NexusMapManager(plugin);
            TheNexus.logger.info("Registering maps...");
            if (NexusMapManager.MAPS_DIRECTORY.listFiles() != null) {
                for (File mapDirectory : NexusMapManager.MAPS_DIRECTORY.listFiles()) {
                    if (!mapDirectory.isDirectory())
                        continue;
                    if (!new File(mapDirectory, "game.yml").exists())
                        continue;
                    String id = mapDirectory.getName();
                    try {
                        NexusGameMap map = mapManager.registerMap(id);
                        TheNexus.logger.info("Registered map: " + map.getOptionalDisplayName().orElse(map.getId()));
                    } catch (Exception ex) {
                        TheNexus.logger.warning("Skipping map " + mapDirectory.getAbsolutePath() + " from registration: " + ex.getMessage());
                    }
                }
            }
            TheNexus.logger.info("Successfully registered " + mapManager.getMaps().size() + " maps!");
            return true;
        } catch (Exception ex) {
            mapManager = current;
            ex.printStackTrace(System.err);
            return false;
        }
    }

    public static NexusMapManager get() {
        if (mapManager == null && !init(TheNexus.instance)) {
            Bukkit.getScheduler().runTask(TheNexus.instance, () -> Bukkit.getPluginManager().disablePlugin(TheNexus.instance));
            throw new IllegalStateException("map manager initialization failed");
        }
        return mapManager;
    }

    /**
     * @param id the id
     * @return the registered nexus map
     */
    public NexusGameMap registerMap(@NotNull String id) {
        NexusGameMap map = new NexusGameMap(id);
        NEXUS_MAPS.put(id, map);
        return map;
    }

    /**
     * @param amount the amount of maps for this choice, where amount <= amount of registered maps
     * @return a MapChoice instance with randomly selected maps
     * @apiNote If the provided amount >= amount of registered mps, the latter will be used instead.
     */
    public MapChoice generateMapChoice(int amount) {
        amount = Math.min(amount, NEXUS_MAPS.size());
        List<NexusGameMap> mapsList = new ArrayList<>(mapManager.NEXUS_MAPS.values());
        List<NexusGameMap> selectedMaps = new ArrayList<>();
        for (int n = 0; n < amount; n++) {
            int index = Util.getRandomInt(mapsList.size());
            selectedMaps.add(mapsList.get(index));
        }
        return new MapChoice(new HashSet<>(selectedMaps));
    }

    /**
     * @return a soft copy of the created maps
     */
    public Map<String, NexusGameMap> getMaps() {
        return Map.copyOf(NEXUS_MAPS);
    }

    /**
     * @return a random registered map, otherwise null if there's none
     */
    @Nullable
    public GameMap getRandom() {
        if (!NEXUS_MAPS.isEmpty()) {
            GameMap[] gameMaps = NEXUS_MAPS.values().toArray(new NexusGameMap[0]);
            return gameMaps[Util.getRandomInt(gameMaps.length)];
        }
        return null;
    }

}
