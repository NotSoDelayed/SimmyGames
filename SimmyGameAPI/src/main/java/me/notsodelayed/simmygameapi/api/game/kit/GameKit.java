package me.notsodelayed.simmygameapi.api.game.kit;

import java.util.List;
import java.util.Map;
import java.util.TreeMap;

import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

/**
 * Represents a kit.
 */
public class GameKit implements Comparable<GameKit> {

    private final TreeMap<Integer, ItemStack> items = new TreeMap<>();
    private final String id;
    private final Material displayItem;
    private String displayName;
    private final String @NotNull [] description;

    protected GameKit(@NotNull String id, @NotNull Material displayItem, @Nullable String displayName, @Nullable String[] description) {
        this.id = id;
        this.displayItem = displayItem;
        this.displayName = displayName != null ? displayName : id;
        this.description = description != null ? description : new String[0];
    }

    /**
     * @return an immutable items mapped to slot index
     */
    public Map<Integer, ItemStack> getItems() {
        return Map.copyOf(items);
    }

    /**
     * @param material the material
     * @return the kit, for chaining
     */
    public GameKit addItem(@Nullable Material material) {
        for (int index = 0; index < Byte.MAX_VALUE; index++) {
            if (getItems().get(index) != null)
                continue;
            setItem(index, new ItemStack(material));
        }
        return this;
    }

    /**
     * Adds items into any empty slot index (from 0)
     * @param materials the materials
     * @return the kit, for chaining
     */
    public GameKit addItems(List<Material> materials) {
        for (Material material : materials) {
            for (int index = 0; index < Byte.MAX_VALUE; index++) {
                if (getItems().get(index) != null)
                    continue;
                if (material != null && !material.equals(Material.AIR)) {
                    setItem(index, new ItemStack(material));
                } else {
                    setItem(index, (Material) null);
                }
            }
        }
        return this;
    }

    /**
     * @param item the item
     * @return the kit, for chaining
     */
    public GameKit addItemStack(@Nullable ItemStack item) {
        for (int index = 0; index < Byte.MAX_VALUE; index++) {
            if (getItems().get(index) != null)
                continue;
            setItem(index, item);
        }
        return this;
    }

    /**
     * Adds items into any empty slot index (from 0)
     * @param items the items
     * @return the kit, for chaining
     */
    public GameKit addItemStacks(List<ItemStack> items) {
        for (ItemStack itemStack : items) {
            for (int index = 0; index < Byte.MAX_VALUE; index++) {
                if (getItems().get(index) != null)
                    continue;
                setItem(index, itemStack.clone());
            }
        }
        return this;
    }

    /**
     * @param slot the slot index
     * @param material the material
     * @return the kit, for chaining
     */
    public GameKit setItem(int slot, @Nullable Material material) {
        if (material != null && !material.equals(Material.AIR))
            items.put(slot, new ItemStack(material));
        else
            items.remove(slot);
        return this;
    }

    /**
     * @param slot the slot index
     * @param item the item
     * @return the kit, for chaining
     */
    public GameKit setItem(int slot, @Nullable ItemStack item) {
        if (item != null && !item.getType().equals(Material.AIR))
            items.put(slot, item.clone());
        else
            items.remove(slot);
        return this;
    }

    /**
     * Sets the slot (from 0 to size of provided list) to the provided items
     * @param materials the materials
     * @return the kit, for chaining
     */
    public GameKit setItems(List<Material> materials) {
        if (!materials.isEmpty())
            for (int index = 0; index < materials.size(); index++)
                setItem(index, new ItemStack(materials.get(index)));
        return this;
    }

    /**
     * Sets the slot (from 0 to size of provided list) to the provided items
     * @param items the items
     * @return the kit, for chaining
     */
    public GameKit setItemStacks(List<ItemStack> items) {
        if (!items.isEmpty())
            for (int index = 0; index < items.size(); index++)
                setItem(index, items.get(index).clone());
        return this;
    }

    public String getId() {
        return id;
    }

    public Material getDisplayItem() {
        return displayItem;
    }

    public String getDisplayName() {
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
