package me.notsodelayed.simmygameapi.api.map;

import java.util.Collection;
import java.util.HashSet;
import java.util.Set;

import me.notsodelayed.simmygameapi.util.Util;
import me.notsodelayed.simmygameapi.api.game.Game;
import org.jetbrains.annotations.Nullable;

/**
 * Represents a map choice for a {@link Game}
 */
public class MapChoice {

    private Set<GameMap> maps = new HashSet<>();

    public MapChoice(@Nullable Set<GameMap> maps) {
        if (maps != null)
            this.maps = maps;
    }

    /**
     * @return a random {@link GameMap} from this choice, or null if no choices available
     */
    @Nullable
    public GameMap getRandom() {
        if (!isEmpty()) {
            GameMap[] mapsArray = maps.toArray(GameMap[]::new);
            return mapsArray[Util.getRandomInt(maps.size())];
        }
        return null;
    }

    /**
     * @return if this map choice contains no maps
     */
    public boolean isEmpty() {
        return maps.isEmpty();
    }

    /**
     * @return a soft copy of map choices
     */
    public Collection<GameMap> getMaps() {
        return Set.copyOf(maps);
    }

    /**
     * @param map the map to add to choices
     * @return true if this set did not already contain the specified map beforehand
     */
    public boolean addMap(GameMap map) {
        return maps.add(map);
    }

    /**
     * @param map the map to remove from choices
     * @return true if this set contained the specified map beforehand
     */
    public boolean removeMap(GameMap map) {
        return maps.remove(map);
    }

    /**
     * @param maps the maps to set the choices
     */
    public void setMaps(Set<GameMap> maps) {
        this.maps = maps;
    }

    /**
     * @param mapChoice the other map choice
     */
    public void setMaps(MapChoice mapChoice) {
        this.maps = new HashSet<>(mapChoice.getMaps());
    }

}
