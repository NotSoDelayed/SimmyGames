package me.notsodelayed.thenexus.kit;

import java.util.Arrays;
import java.util.Optional;

import me.notsodelayed.simmygameapi.api.game.kit.GameKit;
import org.apache.commons.lang3.StringUtils;
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

    public NexusKit(@NotNull String id, @NotNull Material displayItem, @Nullable String[] description) {
        this(id, StringUtils.upperCase(id.replace('_', ' ')), displayItem, description);
    }

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
