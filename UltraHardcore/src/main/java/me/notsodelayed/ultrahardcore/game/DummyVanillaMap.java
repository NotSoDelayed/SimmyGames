package me.notsodelayed.ultrahardcore.game;

import java.io.File;

import me.notsodelayed.simmygameapi.api.map.FixedMap;

/**
 * Represents a map where it doesn't contain the characteristics of a GameMap.
 */
public class DummyVanillaMap extends FixedMap {

    private static final File MAP_DIRECTORY = new File("cache" + File.separator + "dummyMap");

    public DummyVanillaMap() {
        super("vanilla", MAP_DIRECTORY);
    }

    @Override
    protected void loadYaml() {}

    static {
        MAP_DIRECTORY.mkdir();
    }

}
