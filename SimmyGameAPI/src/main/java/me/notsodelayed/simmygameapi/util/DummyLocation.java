package me.notsodelayed.simmygameapi.util;

import com.google.common.base.Preconditions;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.World;

/**
 * Represents a location without a {@link World}.
 */
public class DummyLocation {

    public double x, y, z, yaw, pitch;

    public DummyLocation(double x, double y, double z, double yaw, double pitch) {
        this.x = x;
        this.y = y;
        this.z = z;
        this.yaw = yaw;
        this.pitch = pitch;
    }

    public DummyLocation(double x, double y, double z) {
        this(x, y, z, 0, 0);
    }

    public DummyLocation(Location location) {
        this(location.getX(), location.getY(), location.getZ(), location.getYaw(), location.getPitch());
    }

    /**
     * @param worldName the loaded world name
     * @return the bukkit location with the specified world name
     */
    public Location toBukkitLocation(String worldName) {
        World world = Bukkit.getWorld(worldName);
        Preconditions.checkState(world != null, String.format("world %s is not loaded", worldName));
        return new Location(world, x, y, z);
    }

    /**
     * Centralises this location.
     * @return this instance
     */
    public DummyLocation centralise() {
        x = Math.floor(x) + 0.5;
        y = Math.floor(y) + 0.5;
        z = Math.floor(z) + 0.5;
        return this;
    }

    /**
     * @param obj {@link DummyLocation} or {@link Location} (ignoring world)
     */
    @Override
    public boolean equals(Object obj) {
        if (!(obj instanceof DummyLocation) && !(obj instanceof Location))
            return false;
        if (obj instanceof DummyLocation dummy)
            return x == dummy.x && y == dummy.y && z == dummy.z && yaw == dummy.yaw && pitch == dummy.pitch;
        Location bukkit = ((Location) obj);
        return x == bukkit.getX() && y == bukkit.getY() && z == bukkit.getZ() && yaw == bukkit.getYaw() && pitch == bukkit.getPitch();
    }

    /**
     * @param obj {@link DummyLocation} or {@link Location} (ignoring world)
     */
    public boolean equalsIgnoreYawPitch(Object obj) {
        if (!(obj instanceof DummyLocation) && !(obj instanceof Location))
            return false;
        if (obj instanceof DummyLocation dummy)
            return x == dummy.x && y == dummy.y && z == dummy.z;
        Location bukkit = ((Location) obj);
        return x == bukkit.getX() && y == bukkit.getY() && z == bukkit.getZ();
    }

    public double getX() {
        return x;
    }

    public double getY() {
        return y;
    }

    public double getZ() {
        return z;
    }

    public double getYaw() {
        return yaw;
    }

    public double getPitch() {
        return pitch;
    }

}
