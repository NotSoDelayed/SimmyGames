package me.notsodelayed.thenexus.kit;

import org.apache.commons.lang3.StringUtils;
import org.bukkit.Material;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import me.notsodelayed.simmygameapi.api.game.kit.GameKit;

/**
 * Represents an ordinary nexus kit.
 */
public class NexusKit extends GameKit {

    public NexusKit(@NotNull String id, @NotNull Material displayItem, @Nullable String[] description) {
        super(id, String.join(" ", StringUtils.capitalize(id)), displayItem, description);
    }

}
