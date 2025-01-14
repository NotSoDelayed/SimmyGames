package me.notsodelayed.towerdefense.building;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

import org.bukkit.Location;

/**
 * Represents a building, such as defense buildings, and more in the TowerDefense game.
 */
public abstract class Building {

    private static final Map<Location, Building> BUILDINGS = new HashMap<>();
    private final Location source;
    private final UUID owner;
    private String displayName;
    private final int length, height;

    protected Building(Location source, UUID owner, String displayName, int length, int height) {
        this.source = source;
        this.owner = owner;
        this.displayName = displayName;
        this.length = length;
        this.height = height;
        BUILDINGS.put(source, this);
        placeStructure();
    }

    public abstract void placeStructure();

    public Location getSource() {
        return source;
    }

    public UUID getOwner() {
        return owner;
    }

    public String getDisplayName() {
        return displayName;
    }

    public Building setDisplayName(String displayName) {
        this.displayName = displayName;
        return this;
    }

    public int getLength() {
        return length;
    }

    public int getHeight() {
        return height;
    }

    /**
     * @throws IllegalStateException if !{@link #isValid()}
     */
    public void validate() throws IllegalStateException {
        if (!isValid())
            throw new IllegalStateException("invalid building to conduct operation");
    }

    /**
     * @return whether this building is valid (not destroyed by the owner)
     */
    public boolean isValid() {
        return BUILDINGS.containsValue(this);
    }

}
