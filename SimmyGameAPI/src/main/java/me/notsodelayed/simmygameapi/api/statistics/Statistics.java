package me.notsodelayed.simmygameapi.api.statistics;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

public class Statistics {

    private final Map<String, Object> STATS = new HashMap<>();

    public Statistics() {}

    /**
     * @param key the stats key
     * @param value the integer to add
     * @return the updated integer
     */
    public int addInt(String key, int value) {
        //noinspection DataFlowIssue
        return value + (int) STATS.put(key, value);
    }

    public int getInt(String key) {
        return (int) STATS.get(key);
    }

    /**
     * @return immutable map of registered stats with their respective key and its value
     */
    public Map<String, Object> getAll() {
        return Map.copyOf(STATS);
    }

    public Set<String> getKeys() {
        return Set.copyOf(STATS.keySet());
    }

}
