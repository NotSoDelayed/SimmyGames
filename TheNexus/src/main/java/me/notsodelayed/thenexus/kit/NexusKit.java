package me.notsodelayed.thenexus.kit;

import java.util.Arrays;
import java.util.Optional;

import me.notsodelayed.simmygameapi.api.kit.GameKit;
import org.bukkit.Material;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

/**
 * Represents an ordinary nexus kit.
 */
public class NexusKit extends GameKit {

    private final Material displayItem;
    @Nullable
    private final String[] description;

    public NexusKit(@NotNull String id, @Nullable String displayName, @NotNull Material displayItem, @Nullable String[] description) {
        super(id, displayName);
        this.displayItem = displayItem;
        this.description = description;
    }

    /**
     * @return the display item
     */
    public Material getDisplayItem() {
        return displayItem;
    }

    /**
     * @return the description
     */
    @Nullable
    public String[] getDescription() {
        return description;
    }

    /**
     * @return the optional description
     */
    public Optional<String[]> getOptionalDescription() {
        return Optional.ofNullable(description);
    }

    @Override
    public String toString() {
        return "NexusKit{" +
                "displayItem=" + displayItem +
                ", description=" + Arrays.toString(description) +
                ", parent:[{" + super.toString() + "}]" +
                '}';
    }

}
