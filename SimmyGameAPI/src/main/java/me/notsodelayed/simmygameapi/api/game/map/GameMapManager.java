package me.notsodelayed.simmygameapi.api.game.map;

import java.io.File;
import java.util.*;
import java.util.function.Function;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import me.notsodelayed.simmygameapi.SimmyGameAPI;
import me.notsodelayed.simmygameapi.util.FileUtil;
import me.notsodelayed.simmygameapi.util.Util;

public class GameMapManager<M extends GameMap> {

    private static final List<GameMapManager<?>> MANAGERS = new ArrayList<>();
    private final Map<String, M> maps = new HashMap<>();

    public GameMapManager() {
        MANAGERS.add(this);
    }

    /**
     * Registers maps from a directory containing them. Exceptions occurred during any of the maps will be ignored.
     * @param directory the directory with the maps
     * @param register the register function
     * @return a list of successfully registered maps
     */
    public @NotNull List<M> registerMapsFromDirectory(File directory, Function<File, M> register) {
        FileUtil.checkIsDirectoryOrThrow(directory);
        List<M> maps = new ArrayList<>();
        //noinspection DataFlowIssue
        for (File sub : directory.listFiles()) {
            if (!sub.isDirectory())
                continue;
            try {
                registerMap(register.apply(sub));
            } catch (Exception ex) {
                SimmyGameAPI.logger.warning("[MapManager] Skipping map loading for '" + sub.getPath() + "' due to " + ex.getClass().getSimpleName() + ": " + ex.getMessage());
            }
        }
        return maps;
    }

    /**
     * @param map the map
     * @throws IllegalStateException if registering a map with an existing id, or ,map directory is missing
     */
    public void registerMap(M map) {
        if (maps.containsKey(map.getId()))
            throw new IllegalStateException("map '" + map.getId() + "' already exists");
        if (!map.getDirectory().isDirectory())
            throw new IllegalStateException("directory of map '" + map.getId() + "' is missing");
        maps.put(map.getId(), map);
    }

    public void unregisterMap(M map) {
        maps.remove(map.getId());
    }

    public int size() {
        return maps.size();
    }

    public @Nullable M getMap(String id) {
        return maps.get(id);
    }

    public Map<String, M> getMaps() {
        return Map.copyOf(maps);
    }

    public @NotNull List<M> randomChoices(int amount) {
        if (size() < 0)
            throw new IllegalStateException("no maps available for choices");
        if (amount < 0)
            throw new ArrayIndexOutOfBoundsException("amount cannot be less than 0");
        if (amount > size())
            throw new ArrayIndexOutOfBoundsException("amount cannot be more than the registered maps");
        List<M> mapsList = new ArrayList<>(maps.values());
        List<M> choices = new ArrayList<>();
        for (int i = 0; i < amount; i++) {
            M map = mapsList.get(Util.getRandomInt(mapsList.size()));
            choices.add(map);
        }
        return choices;
    }

    public static @NotNull List<GameMapManager<?>> getManagers() {
        return List.copyOf(MANAGERS);
    }

    /**
     * @param mapType the map type
     * @return an immutable list of managers of the map type
     * @apiNote Managers without any elements will be ignored, as there's no way to validate its type without a value to check with
     */
    public static <T extends GameMap> @NotNull List<GameMapManager<T>> getManagers(Class<T> mapType) {
        return getManagers().stream()
                .filter(manager -> {
                    if (manager.size() == 0)
                        return false;
                    return manager.randomChoices(1).getFirst().getClass().equals(mapType);
                })
                .map(manager -> (GameMapManager<T>) manager)
                .toList();
    }

}
