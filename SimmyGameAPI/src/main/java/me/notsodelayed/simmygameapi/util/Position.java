package me.notsodelayed.simmygameapi.util;

import com.google.common.base.Preconditions;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.World;
import org.jetbrains.annotations.NotNull;

/**
 * Represents a location without a {@link World}.
 */
public class Position implements Cloneable {
    private double x, y, z;
    private final float yaw, pitch;

    public Position(double x, double y, double z, float yaw, float pitch) {
        this.x = x;
        this.y = y;
        this.z = z;
        this.yaw = yaw;
        this.pitch = pitch;
    }

    public Position(double x, double y, double z) {
        this(x, y, z, 0f, 0f);
    }

    public Position(Location location) {
        this(location.getX(), location.getY(), location.getZ(), location.getYaw(), location.getPitch());
    }

    public static Position fromString(@NotNull String string) {
        double x, y, z;
        float yaw = 0, pitch = 0;
        String[] data = string.split(",");
        if (data.length != 3 && data.length != 5)
            throw new IllegalArgumentException("provided string does not represent a Position");

        try {
            x = Double.parseDouble(data[0]);
            y = Double.parseDouble(data[1]);
            z = Double.parseDouble(data[2]);
            if (data.length == 5) {
                yaw = Float.parseFloat(data[3]);
                pitch = Float.parseFloat(data[4]);
            }
        } catch (IllegalArgumentException ex) {
            throw new IllegalArgumentException("provided string does not represent a Position", ex);
        }
        return new Position(x, y, z, yaw, pitch);
    }

    /**
     * @param world the bukkit world
     * @return the bukkit location
     */
    public Location toBukkitLocation(World world) {
        Preconditions.checkArgument(world != null, "world is null");
        return new Location(world, x, y, z, yaw, pitch);
    }

    /**
     * @param worldName the loaded world name
     * @return the bukkit location with the specified world name
     */
    public Location toBukkitLocation(String worldName) {
        World world = Bukkit.getWorld(worldName);
        Preconditions.checkState(world != null, "world '" + worldName + "' is not loaded");
        return toBukkitLocation(world);
    }

    /**
     * Centralises this location.
     * @return this instance
     */
    public Position centralise() {
        x = Math.floor(x) + 0.5;
        y = Math.floor(y) + 0.5;
        z = Math.floor(z) + 0.5;
        return this;
    }

    /**
     * @param obj {@link Position} or {@link Location} (ignores world)
     */
    @Override
    public boolean equals(Object obj) {
        if (!(obj instanceof Position) && !(obj instanceof Location))
            return false;
        if (obj instanceof Position dummy)
            return x == dummy.x && y == dummy.y && z == dummy.z && yaw == dummy.yaw && pitch == dummy.pitch;
        Location bukkit = ((Location) obj);
        return x == bukkit.getX() && y == bukkit.getY() && z == bukkit.getZ() && yaw == bukkit.getYaw() && pitch == bukkit.getPitch();
    }

    /**
     * @param obj {@link Position} or {@link Location} (ignores world)
     */
    public boolean equalsIgnoreYawPitch(Object obj) {
        if (!(obj instanceof Position) && !(obj instanceof Location))
            return false;
        if (obj instanceof Position dummy)
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

    @Override
    public String toString() {
        return "Position{" +
                "x=" + x +
                ", y=" + y +
                ", z=" + z +
                ", yaw=" + yaw +
                ", pitch=" + pitch +
                '}';
    }

    @Override
    public Position clone() {
        try {
            return (Position) super.clone();
        } catch (CloneNotSupportedException e) {
            throw new AssertionError();
        }
    }

}
