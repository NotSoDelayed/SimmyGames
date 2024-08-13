package me.notsodelayed.thenexus.kit;

import org.bukkit.Material;
import org.bukkit.potion.PotionEffect;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

/**
 * Represents a nexus kit with potion effects applied via a trigger, such as sneaking for X seconds.
 */
public class TriggerPotionNexusKit extends PotionNexusKit {

    public enum TriggerAction {

        SNEAK("sneak"),
        UNKNOWN("unknown");

        private String toString;

        TriggerAction(String toString) {
            this.toString = toString;
        }

        @Override
        public String toString() {
            return toString;
        }

    }

    private final TriggerAction triggerAction;

    public TriggerPotionNexusKit(@NotNull String id, @Nullable String displayName, @NotNull Material displayItem, @Nullable String[] description, @NotNull PotionEffect[] potionEffects, @NotNull TriggerAction triggerAction) {
        super(id, displayName, displayItem, description, potionEffects);
        this.triggerAction = triggerAction;
    }

    /**
     * @return the trigger action to activate the potion effect
     */
    public TriggerAction getTriggerAction() {
        return triggerAction;
    }

}
