package me.notsodelayed.simmygameapi.util;

import com.google.common.base.Preconditions;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.World;

/**
 * Represents a location without a {@link World}.
 */
public class LazyLocation {

    private double x, y, z;
    private float yaw, pitch;

    public LazyLocation(double x, double y, double z, float yaw, float pitch) {
        this.x = x;
        this.y = y;
        this.z = z;
        this.yaw = yaw;
        this.pitch = pitch;
    }

    public LazyLocation(double x, double y, double z) {
        this(x, y, z, 0f, 0f);
    }

    public LazyLocation(Location location) {
        this(location.getX(), location.getY(), location.getZ(), location.getYaw(), location.getPitch());
    }

    /**
     * @param world the bukkit world
     * @return the bukkit location
     */
    public Location toBukkitLocation(World world) {
        return new Location(world, x, y, z, yaw, pitch);
    }

    /**
     * @param worldName the loaded world name
     * @return the bukkit location with the specified world name
     */
    public Location toBukkitLocation(String worldName) {
        World world = Bukkit.getWorld(worldName);
        Preconditions.checkState(world != null, String.format("world %s is not loaded", worldName));
        return toBukkitLocation(world);
    }

    /**
     * Centralises this location.
     * @return this instance
     */
    public LazyLocation centralise() {
        x = Math.floor(x) + 0.5;
        y = Math.floor(y) + 0.5;
        z = Math.floor(z) + 0.5;
        return this;
    }

    /**
     * @param obj {@link LazyLocation} or {@link Location} (ignoring world)
     */
    @Override
    public boolean equals(Object obj) {
        if (!(obj instanceof LazyLocation) && !(obj instanceof Location))
            return false;
        if (obj instanceof LazyLocation dummy)
            return x == dummy.x && y == dummy.y && z == dummy.z && yaw == dummy.yaw && pitch == dummy.pitch;
        Location bukkit = ((Location) obj);
        return x == bukkit.getX() && y == bukkit.getY() && z == bukkit.getZ() && yaw == bukkit.getYaw() && pitch == bukkit.getPitch();
    }

    /**
     * @param obj {@link LazyLocation} or {@link Location} (ignoring world)
     */
    public boolean equalsIgnoreYawPitch(Object obj) {
        if (!(obj instanceof LazyLocation) && !(obj instanceof Location))
            return false;
        if (obj instanceof LazyLocation dummy)
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

    public float getYaw() {
        return yaw;
    }

    public float getPitch() {
        return pitch;
    }

}
