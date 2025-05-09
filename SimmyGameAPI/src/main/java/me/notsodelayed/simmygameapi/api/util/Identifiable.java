package me.notsodelayed.simmygameapi.api.util;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

/**
 * Represents an object with identity (id, display name).
 */
public interface Identifiable {

    /**
     * @return the unique id of this object
     */
    @NotNull String id();

    /**
     * @return the human-readable name of this object
     */
    @Nullable String displayName();

    /**
     * @return {@link #displayName()} ?: {@link #id()}
     */
    default String displayNameOrId() {
        return displayName() != null ? displayName() : id();
    }

}
