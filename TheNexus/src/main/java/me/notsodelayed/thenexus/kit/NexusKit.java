package me.notsodelayed.thenexus.kit;

import java.util.Arrays;
import java.util.Optional;

import me.notsodelayed.simmygameapi.api.game.kit.GameKit;
import org.apache.commons.lang.StringUtils;
import org.bukkit.Material;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

/**
 * Represents an ordinary nexus kit.
 */
public class NexusKit extends GameKit {

    public NexusKit(@NotNull String id, @NotNull Material displayItem, @Nullable String[] description) {
        this(id, StringUtils.upperCase(id.replace('_', ' ')), displayItem, description);
    }

    public NexusKit(@NotNull String id, @Nullable String displayName, @NotNull Material displayItem, @Nullable String[] description) {
        super(id, displayItem, displayName, description);
    }

}
