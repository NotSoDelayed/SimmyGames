package me.notsodelayed.simmygameapi.api.data;

public class Key<T> {

    private final String key;
    private final Class<T> clazz;

    public Key(String key, Class<T> clazz) {
        this.key = key;
        this.clazz = clazz;
    }

    /**
     * @return the string key
     */
    public String getKey() {
        return key;
    }

    /**
     * @return the value type
     */
    public Class<T> getValueType() {
        return clazz;
    }

}
