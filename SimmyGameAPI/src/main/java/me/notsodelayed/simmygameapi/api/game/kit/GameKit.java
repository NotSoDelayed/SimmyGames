package me.notsodelayed.simmygameapi.api.game.kit;

import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.TreeMap;

import me.notsodelayed.simmygameapi.util.Util;
import org.apache.commons.lang3.StringUtils;
import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

/**
 * Represents a kit.
 */
public abstract class GameKit {

    private final TreeMap<Integer, ItemStack> items = new TreeMap<>();
    private final String id;
    private String displayName;

    /**
     * @param id the id
     * @param displayName the display name
     */
    protected GameKit(@NotNull String id, @Nullable String displayName) {
        this.id = id;
        this.displayName = displayName;
    }

    /**
     * @return an immutable items mapped to slot index
     */
    public Map<Integer, ItemStack> getItems() {
        return Map.copyOf(items);
    }

    /**
     * @param slot the slot index, where 0 >= index <= 40
     * @param material the material, or null to clear the assignment
     * @return the kit, for chaining
     */
    public GameKit setItem(int slot, @Nullable Material material) {
        if (!Util.isNumberWithin(0, 40, slot))
            throw new IllegalArgumentException("kit slot index must be within 0 and 40");
        if (material != null && !material.equals(Material.AIR))
            items.put(slot, new ItemStack(material));
        else
            items.remove(slot);
        return this;
    }

    /**
     * @param slot the slot index, where 0 >= index <= 40
     * @param item the item, or null to clear the assignment
     * @return the kit, for chaining
     */
    public GameKit setItem(int slot, @Nullable ItemStack item) {
        if (!Util.isNumberWithin(0, 40, slot))
            throw new IllegalArgumentException("kit slot index must be within 0 and 40");
        if (item != null && !item.getType().equals(Material.AIR))
            items.put(slot, item.clone());
        else
            items.remove(slot);
        return this;
    }

    /**
     * Sets the slot (from 0 to size of provided list) to the provided items
     * @param items the items
     * @return the kit, for chaining
     */
    public GameKit setItems(List<ItemStack> items) {
        if (!items.isEmpty())
            for (int index = 0; index < items.size(); index++)
                setItem(index, items.get(index).clone());
        return this;
    }

    /**
     * @return the id
     */
    public String getId() {
        return id;
    }

    /**
     * @return the optional display name
     */
    public Optional<String> getDisplayName() {
        return Optional.ofNullable(displayName);
    }

    /**
     * @param displayName the display name
     */
    public void setDisplayName(@Nullable String displayName) {
        this.displayName = displayName;
    }

    @Override
    public String toString() {
        return "GameKit{" +
                "items=" + StringUtils.joinWith(",", items.values()) +
                ", id='" + id + '\'' +
                ", displayName='" + displayName + '\'' +
                '}';
    }
}
