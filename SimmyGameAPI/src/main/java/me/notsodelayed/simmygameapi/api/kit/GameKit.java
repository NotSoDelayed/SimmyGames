package me.notsodelayed.simmygameapi.api.kit;

import java.util.HashMap;
import java.util.Map;

import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.potion.PotionEffect;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

/**
 * Represents a kit.
 */
public class GameKit implements Comparable<GameKit> {

    private final Map<Integer, ItemStack> items = new HashMap<>();
    private final String id;
    private final Material displayItem;
    private @Nullable String displayName;
    private final String @NotNull [] description;
    private final PotionEffect[] potionEffects;

    public GameKit(@NotNull String id, @Nullable String displayName, @NotNull Material displayItem, @Nullable String[] description) {
        this(id, displayName, displayItem, description, null);
    }

    public GameKit(@NotNull String id, @Nullable String displayName, @NotNull Material displayItem, @Nullable String[] description, @Nullable PotionEffect[] potionEffects) {
        this.id = id;
        this.displayName = displayName;
        this.displayItem = displayItem;
        this.description = description != null ? description : new String[0];
        this.potionEffects = potionEffects != null ? potionEffects : new PotionEffect[0];
    }

    /**
     * Gives this kit to a player.
     * @param player the player
     * @implNote Developers may override this for custom kit perks
     */
    public void give(@NotNull Player player) {
        for (Map.Entry<Integer, ItemStack> entry : items.entrySet()) {
            try {
                player.getInventory().setItem(entry.getKey(), entry.getValue());
            } catch (ArrayIndexOutOfBoundsException ignored) {}
        }
        for (PotionEffect potion : potionEffects)
            player.addPotionEffect(potion);
    }

    public GameKit slot(int index, Material material) {
        items.put(index, new ItemStack(material));
        return this;
    }

    public GameKit slot(int index, ItemStack itemStack) {
        items.put(index, itemStack.clone());
        return this;
    }

    public GameKit items(Material... materials) {
        for (Material material : materials) {
            for (int index = 0; index <= 40; index++) {
                if (items.containsKey(index))
                    continue;
                slot(index, material);
                break;
            }
        }
        return this;
    }

    public GameKit items(ItemStack... itemStacks) {
        for (ItemStack itemStack : itemStacks) {
            for (int index = 0; index <= 40; index++) {
                if (items.containsKey(index))
                    continue;
                slot(index, itemStack);
                break;
            }
        }
        return this;
    }

    /**
     * @return an immutable items mapped to slot index
     */
    public Map<Integer, ItemStack> items() {
        return Map.copyOf(items);
    }

    public String getId() {
        return id;
    }

    public Material getDisplayItem() {
        return displayItem;
    }

    public @Nullable String getDisplayName() {
        return displayName;
    }

    public void setDisplayName(@Nullable String displayName) {
        this.displayName = displayName;
    }

    public String @NotNull [] getDescription() {
        return description;
    }

    @Override
    public int compareTo(@NotNull GameKit kit) {
        return this.id.compareTo(kit.id);
    }

}
