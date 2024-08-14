package me.notsodelayed.simmygameapi.api.registry;

import java.io.File;
import java.util.HashMap;
import java.util.Map;

import org.bukkit.plugin.java.JavaPlugin;
import org.jetbrains.annotations.ApiStatus;
import org.jetbrains.annotations.NotNull;

import me.notsodelayed.simmygameapi.api.registry.parser.Node;

// TODO finish this shit
@ApiStatus.Experimental
public abstract class Registry<T> {

    private static Map<JavaPlugin, Map<String, Registry<?>>> kitRegistryMap;
    private final JavaPlugin plugin;

    private Map<String, Node<?>> nodes;

    /**
     * @param plugin the belonging plugin of this registry
     * @param id the registry id
     * @throws IllegalArgumentException if plugin has an existing registry with this id
     */
    public Registry(JavaPlugin plugin, String id) throws IllegalArgumentException {
        if (kitRegistryMap == null)
            kitRegistryMap = new HashMap<>();
        Map<String, Registry<?>> pluginKitRegistry = kitRegistryMap.computeIfAbsent(plugin, p -> new HashMap<>());
        if (pluginKitRegistry.containsKey(id))
            throw new IllegalArgumentException("Registry id '" + id + "' already exists");
        this.plugin = plugin;
        pluginKitRegistry.put(id, this);
    }

    /**
     * Adds simple parsing nodes where it does not involve complex computing and list nodes from YMLs.
     * <p>i.e. display-name (takes in a simple string), material (takes in a simple {@link org.bukkit.Material}), etc.</p>
     * @param nodes the node(s)
     * @return this instance
     */
    public Registry<T> addSimpleParsing(@NotNull Node<?>... nodes) {
        return this;
    }

    /**
     * Parses the YML elements with the registered {@link Node Node(s)}.
     */
    public abstract Map<String, T> parse();

    /**
     * @return the directory of where the YMLs of this registry reads.
     */
    public abstract File getDataDirectory();

    /**
     * @return the plugin of this registry
     */
    public JavaPlugin getPlugin() {
        return plugin;
    }

}
