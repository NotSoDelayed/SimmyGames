package me.notsodelayed.simmygameapi.api.kit.exception;

import me.notsodelayed.simmygameapi.api.kit.GameKit;
import org.jetbrains.annotations.NotNull;

/**
 * An exception for whenever an error occurred in {@link GameKit} related operation.
 */
public class KitException extends RuntimeException {

    private GameKit kit = null;
    private String kitName = null;

    public KitException(@NotNull GameKit kit, @NotNull String message) {
        super(message);
        this.kit = kit;
    }

    public KitException(@NotNull String kitName, @NotNull String message) {
        super(message);
        this.kitName = kitName;
    }

    /**
     * @return the affected kit
     */
    @NotNull
    public String getKitName() {
        if (kit != null)
            return kit.getOptionalDisplayName().orElse(kit.getId());
        return kitName;
    }

}
