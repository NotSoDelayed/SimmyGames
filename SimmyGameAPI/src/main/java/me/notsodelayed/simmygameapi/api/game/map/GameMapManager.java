package me.notsodelayed.simmygameapi.api.game.map;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import me.notsodelayed.simmygameapi.util.Util;

public class GameMapManager<M extends GameMap> {

    private final Map<String, M> maps = new HashMap<>();

    public GameMapManager() {}

    public void registerMap(M map) {
        maps.put(map.getId(), map);
    }

    public void unregisterMap(M map) {
        maps.remove(map.getId());
    }

    public int size() {
        return maps.size();
    }

    public Map<String, M> getMaps() {
        return Map.copyOf(maps);
    }

    public Set<M> randomChoices(int amount) {
        if (amount < 0)
            throw new ArrayIndexOutOfBoundsException("amount cannot be less than 0");
        if (amount > size())
            throw new ArrayIndexOutOfBoundsException("amount cannot be more than the registered maps");
        List<M> mapsList = new ArrayList<>(maps.values());
        Set<M> choices = new HashSet<>();
        for (int i = 0; i < amount; i++) {
            M map = mapsList.get(Util.getRandomInt(mapsList.size()));
            choices.add(map);
            mapsList.remove(map);
        }
        return choices;
    }

}
