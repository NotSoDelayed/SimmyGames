package me.notsodelayed.thenexus.kit;

import org.bukkit.Material;
import org.bukkit.potion.PotionEffect;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

/**
 * Represents a nexus kit with default potion effects applied.
 */
public class PotionNexusKit extends NexusKit {

    private final PotionEffect[] potionEffects;

    public PotionNexusKit(@NotNull String id, @Nullable String displayName, @NotNull Material displayItem, @Nullable String[] description, @Nullable PotionEffect[] potionEffects) {
        super(id, displayName, displayItem, description);
        this.potionEffects = potionEffects;
    }

    /**
     * @return the potion effects applied for this kit
     */
    @Nullable
    public PotionEffect[] getPotionEffects() {
        return potionEffects;
    }

}
