package me.notsodelayed.simmygameapi.api.game.map;

import java.io.File;
import java.util.Map;
import java.util.Optional;
import java.util.Set;

import org.bukkit.configuration.file.YamlConfiguration;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import me.notsodelayed.simmygameapi.SimmyGameAPI;
import me.notsodelayed.simmygameapi.api.game.Game;
import me.notsodelayed.simmygameapi.api.util.Node;
import me.notsodelayed.simmygameapi.util.FileUtil;

/**
 * Represents a map for a {@link Game}.
 * Developers shall extend from this class to make use of the current implementation for their game map.
 */
public class GameMap {

    private final String id;
    @Nullable
    private String displayName;
    private final File mapDirectory;
    private final File yamlFile;
    private YamlConfiguration yaml;
    private Map<String, Object> cachedNodes;

    public GameMap(@NotNull String id, @Nullable String displayName, @NotNull File mapDirectory) {
        this.id = id;
        this.displayName = displayName;
        FileUtil.checkIsDirectoryOrThrow(mapDirectory);
        this.mapDirectory = mapDirectory;
        yamlFile = new File(mapDirectory, "map.yml");
    }

    public GameMap(@NotNull String id, @NotNull File mapDirectory) {
        this(id, null, mapDirectory);
        if (!loadYamlFromFile())
            return;
        displayName = yaml.getString("display-name");
    }

    public boolean loadYamlFromFile() {
        YamlConfiguration oldYaml = yaml;
        try {
            if (yamlFile.exists()) {
                yaml = YamlConfiguration.loadConfiguration(yamlFile);
                for (Node<?> node : getDataNodes()) {
                    cachedNodes.put(node.toString(), node.evaluate());
                }
                return true;
            }
            SimmyGameAPI.logger.warning(String.format("%s has missing 'map.yml' to load custom data. Skipping custom data...", this.getDisplayName().orElse(this.getId())));
            return false;
        } catch (Exception ex) {
            String warning = oldYaml != null ? "the previous YAML data will remain in use" : "any custom data will be unavailable";
            yaml = oldYaml;
            SimmyGameAPI.logger.warning(String.format("Exception occurred whilst loading map.yml of %s '%s' -- %s.", this.getClass().getSimpleName(), this.id, warning));
            return false;
        }
    }

    /**
     * @return the custom nodes of the YAML of this map
     * @apiNote The default nodes (display-name) are excluded
     * @implNote Developers may override this method to include custom nodes
     */
    public Set<Node<?>> getDataNodes() {
        return Set.of();
    }

    /**
     * @return the immutable map of the cached parsed nodes
     */
    public Map<String, Object> getCachedNodes() {
        return Map.copyOf(cachedNodes);
    }

    /**
     * @return the optional display name
     */
    public Optional<String> getDisplayName() {
        return Optional.ofNullable(displayName);
    }

    /**
     * @return the id
     */
    public String getId() {
        return id;
    }

    /**
     * @return the directory of this map
     */
    public File getDirectory() {
        return mapDirectory;
    }

    /**
     * @return the YAML file of this map (map.yml)
     */
    public File getYamlFile() {
        return yamlFile;
    }

    /**
     * @return the YAML instance associated
     */
    public YamlConfiguration getYaml() {
        return yaml;
    }

}
