package me.notsodelayed.simmygameapi.api.registry;

import java.io.File;

import me.notsodelayed.simmygameapi.api.exception.InvalidYamlException;
import me.notsodelayed.simmygameapi.api.kit.GameKit;
import org.bukkit.configuration.file.YamlConfiguration;
import org.jetbrains.annotations.NotNull;

/**
 * Manages {@link GameKit}, such as registrations and kit YAML parsing.
 * @param <K> the super type of your kit type
 */
public abstract class GameKitRegistry<K extends GameKit> {

    /**
     * @return the directory of where the kit YMLs houses.
     */
    abstract File getKitsDirectory();

    /**
     * Registers a kit from a {@link YamlConfiguration}.
     * @param ymlFile the yml file
     * @return {@link K} – the registered kit
     * @throws IllegalArgumentException if ymlFile is not a YML file, or is a disabled kit YML
     * @throws InvalidYamlException if ymlFile is not a valid kit yml, or a half-baked kit yml
     */
    abstract K registerKit(@NotNull File ymlFile) throws IllegalArgumentException, InvalidYamlException;

}
