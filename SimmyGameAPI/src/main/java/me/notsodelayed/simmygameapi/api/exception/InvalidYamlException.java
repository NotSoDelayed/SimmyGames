package me.notsodelayed.simmygameapi.api.exception;

import org.jetbrains.annotations.NotNull;

/**
 * Thrown whenever parsing an invalid kit YAML.
 */
public class InvalidYamlException extends RuntimeException {

    public InvalidYamlException(@NotNull String objectId) {
        super("invalid format for '" + objectId + "'");
    }

    /**
     * Exception constructor for when a YAML key is expected a different value
     * @param subjectId the subject ID
     * @param key the key
     * @param expectedValue the expected value
     * @param fetchedValue the fetched value
     */
    public InvalidYamlException(@NotNull String subjectId, @NotNull String key, @NotNull String expectedValue, @NotNull String fetchedValue) {
        super("invalid key " + key + " for '" + subjectId + "': expected " + expectedValue + " (got " + fetchedValue + ")");
    }

}
