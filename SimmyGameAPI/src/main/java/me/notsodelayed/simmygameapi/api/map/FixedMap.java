package me.notsodelayed.simmygameapi.api.map;

import java.io.File;

import org.bukkit.configuration.file.YamlConfiguration;
import org.jetbrains.annotations.NotNull;
import org.yaml.snakeyaml.error.YAMLException;

import me.notsodelayed.simmygameapi.SimmyGameAPI;
import me.notsodelayed.simmygameapi.api.GameMap;
import me.notsodelayed.simmygameapi.util.FileUtil;

/**
 * Represents a {@link GameMap} with a pre-built world.
 */
public class FixedMap extends GameMap {

    private final File fileLocation;
    private YamlConfiguration yaml;

    /**
     * @throws YAMLException if the destination directory does not have a 'map.yml'
     */
    public FixedMap(@NotNull String id, @NotNull File fileLocation) {
        super(id);
        FileUtil.checkIsDirectoryOrThrow(fileLocation);
        this.fileLocation = fileLocation;
    }

    protected void loadYaml() {
        File ymlFile = new File(fileLocation, "map.yml");
        yaml = new YamlConfiguration();
        if (ymlFile.exists()) {
            try {
                yaml.load(ymlFile);
            } catch (Exception ex) {
                SimmyGameAPI.logger.warning("Exception occurred whilst reading map data of " + fileLocation.getPath() + " -- Proceeding without any custom data...");
                ex.printStackTrace(System.err);
            }
        } else {
            throw new YAMLException("Missing 'map.yml' file");
        }
    }

    /**
     * @return the file location of this map
     */
    public File fileLocation() {
        return fileLocation;
    }

    protected YamlConfiguration yaml() {
        return yaml;
    }

}
