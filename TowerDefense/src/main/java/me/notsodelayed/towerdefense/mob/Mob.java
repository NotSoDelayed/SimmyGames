package me.notsodelayed.towerdefense.mob;

import java.util.UUID;
import java.util.WeakHashMap;
import java.util.function.Consumer;

import com.google.common.base.Preconditions;
import org.bukkit.Location;
import org.bukkit.entity.EntityType;
import org.bukkit.event.entity.CreatureSpawnEvent;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

/**
 * Represents a mob of a TowerDefense game.
 */
public abstract class Mob {

    private static final WeakHashMap<org.bukkit.entity.Mob, Mob> BUKKIT_MOBS = new WeakHashMap<>();
    private final UUID owner;
    private final org.bukkit.entity.Mob bukkitMob;

    /**
     * @param owner the mob owner
     * @param type the mob type
     * @param location location for the mob to spawn
     * @param mapper function for modifying the mob's characteristics
     */
    protected Mob(UUID owner, EntityType type, Location location, Consumer<org.bukkit.entity.Mob> mapper) {
        this.owner = owner;
        bukkitMob = (org.bukkit.entity.Mob) location.getWorld().spawnEntity(location, type, CreatureSpawnEvent.SpawnReason.CUSTOM, entity -> {
            if (!(entity instanceof org.bukkit.entity.Mob mob))
                throw new IllegalStateException("mob type is not a mob");
            // TODO health rework
            mob.setMaxHealth(factoryHealth());
            mob.setHealth(factoryHealth());
            mapper.accept(mob);
        });
        BUKKIT_MOBS.put(bukkitMob, this);
    }

    public static @Nullable Mob getFrom(org.bukkit.entity.Mob bukkitMob) {
        return BUKKIT_MOBS.get(bukkitMob);
    }

    public abstract void tick();

    public abstract double factoryHealth();

    /**
     * @return the bukkit mob associated
     * @throws IllegalStateException if the bukkit mob associated is dead
     */
    public @NotNull org.bukkit.entity.Mob getBukkitMob() throws IllegalStateException {
        Preconditions.checkState(isValid(), "mob is dead");
        return bukkitMob;
    }

    /**
     * @return whether this mob is valid (the bukkit mob associated is alive)
     */
    public boolean isValid() {
        return BUKKIT_MOBS.get(bukkitMob) != null || !bukkitMob.isDead();
    }

    public UUID getOwner() {
        return owner;
    }

    public EntityType getType() {
        return bukkitMob.getType();
    }

}
