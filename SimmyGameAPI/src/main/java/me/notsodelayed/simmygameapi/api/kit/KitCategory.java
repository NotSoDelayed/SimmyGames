package me.notsodelayed.simmygameapi.api.kit;

import org.jetbrains.annotations.Nullable;

/**
 * Represents a kit category.
 */
public class KitCategory {

    public static final KitCategory UNCATEGORIZED = new KitCategory("uncategorized", "uncategorized");

    private final String id;
    private String displayName;

    protected KitCategory(String id, @Nullable String displayName) {
        this.id = id;
        this.displayName = displayName;
    }

    /**
     * @return the id
     */
    public String getId() {
        return id;
    }

    /**
     * @return the display name
     */
    public String getDisplayName() {
        return displayName;
    }

}
