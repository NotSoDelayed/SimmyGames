package me.notsodelayed.simmygameapi.api.statistics;

import java.util.HashMap;
import java.util.Map;
import java.util.Set;

import org.jetbrains.annotations.Nullable;

public class Statistics {

    private final Map<String, Object> STATS = new HashMap<>();

    public Statistics() {}

    private @Nullable Object put(String key, @Nullable Object value) {
        if (value == null) {
            Object previous = get(key);
            STATS.remove(key);
            return previous;
        }
        return STATS.put(key, value);
    }

    /**
     * @param key the stats key
     * @param value the integer to put
     * @return the integer (or the updated integer if this key has an existing integer)
     */
    public Integer putInt(String key, @Nullable Integer value) {
        Integer current = getInt(key);
        if (current != null)
            value += current;
        put(key, value);
        return value;
    }

    /**
     * @param key the stats key
     * @param value the string to put
     * @return the previous string if there's an existing value, otherwise null
     */
    public @Nullable String putString(String key, String value) {
        Object previous = put(key, value);
        return previous != null ? (String) previous : null;
    }

    private @Nullable Object get(String key) {
        return STATS.get(key);
    }

    public @Nullable Integer getInt(String key) {
        Object value = get(key);
        return value != null ? (Integer) value : null;
    }

    public @Nullable String getString(String key) {
        Object value = get(key);
        return value != null ? (String) value : null;
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
