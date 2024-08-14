package me.notsodelayed.simmygameapi.api.registry;

import java.io.File;
import java.util.Map;

import org.bukkit.plugin.java.JavaPlugin;
import org.jetbrains.annotations.ApiStatus;

import me.notsodelayed.simmygameapi.api.game.kit.GameKit;

// TODO make this shit work
@ApiStatus.Experimental
public class GameKitRegistry<K extends GameKit> extends Registry<K> {

    private final File kitsDirectory;

    /**
     * @param plugin the belonging plugin of this registry
     * @param id     the registry id
     * @throws IllegalArgumentException if plugin has an existing registry with this id
     */
    public GameKitRegistry(JavaPlugin plugin, String id) throws IllegalArgumentException {
        super(plugin, id);
        kitsDirectory = new File(plugin.getDataFolder(), "kits");
    }

    @Override
    public Map<String, K> parse() {
        // TODO make this shit work
        return null;
    }

    @Override
    public File getDataDirectory() {
        return kitsDirectory;
    }

}
