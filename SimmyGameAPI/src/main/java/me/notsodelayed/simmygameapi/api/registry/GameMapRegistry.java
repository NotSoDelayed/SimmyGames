package me.notsodelayed.simmygameapi.api.registry;

import java.io.File;
import java.util.List;
import java.util.Map;

import me.notsodelayed.simmygameapi.api.map.GameMap;
import org.bukkit.plugin.java.JavaPlugin;
import org.jetbrains.annotations.ApiStatus;

// TODO make this shit work
@ApiStatus.Internal
public class GameMapRegistry<M extends GameMap> extends Registry<M> {

    private final File mapsDirectory;

    /**
     * @param plugin the belonging plugin of this registry
     * @param id     the registry id
     * @throws IllegalArgumentException if plugin has an existing registry with this id
     */
    public GameMapRegistry(JavaPlugin plugin, String id) throws IllegalArgumentException {
        super(plugin, id);
        mapsDirectory = new File(plugin.getDataFolder(), "maps");
    }

    @Override
    public Map<String, M> parse() {
        // TODO make this shit work
        return null;
    }

    /**
     * @return the directory of where the {@link GameMap GameMap(s)} houses
     */
    public File getMapsDirectory() {
        return mapsDirectory;
    }

    /**
     * @deprecated in favour of {@link #getMapsDirectory()}
     * <p>this particular registry houses {@link GameMap GameMap(s)} directories which houses its respective data YMLs, thus requires special handling.</p>
     * @see #getMapsDirectory()
     */
    @Deprecated
    @Override
    public File getDataDirectory() {
        return getMapsDirectory();
    }

}
