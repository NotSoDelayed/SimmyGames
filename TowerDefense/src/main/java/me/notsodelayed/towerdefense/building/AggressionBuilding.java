package me.notsodelayed.towerdefense.building;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;
import java.util.concurrent.CopyOnWriteArrayList;

import com.destroystokyo.paper.ParticleBuilder;
import org.bukkit.*;
import org.bukkit.entity.LivingEntity;
import org.bukkit.util.Vector;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import me.notsodelayed.towerdefense.Config;
import me.notsodelayed.towerdefense.TowerDefense;
import me.notsodelayed.towerdefense.mob.Mob;

/**
 * Represents a building which emits attacks.
 */
public class AggressionBuilding extends Building {

    public enum Type {
        DIRECT_RANGE("direct range"),
        AREA("area");

        final String toString;
        Type(String toString) {
            this.toString = toString;
        }

        @Override
        public String toString() {
            return toString;
        }
    }

    private @Nullable ParticleBuilder particleBuilder;
    private int radius, damage, minTargets = 1, maxTargets = 1, frequency, ammo, maxAmmo;
    private @Nullable Sound sound;
    private final CopyOnWriteArrayList<Mob> targets = new CopyOnWriteArrayList<>();
    private long lastAttacked = 0;

    /**
     * @param source the center of the base of the building
     * @param owner the building placer
     * @param displayName the display name
     * @param length the width and length of the building (for structure)
     * @param height the height of the building (for structure)
     * @apiNote Required setters to use upon calling constructor:
     * <p>
     * {@link #radius(int)}, {@link #frequency(int)}
     */
    protected AggressionBuilding(Location source, UUID owner, String displayName, int length, int height) {
        super(source, owner, displayName, length, height);
        Bukkit.getScheduler().runTaskTimer(TowerDefense.instance, task -> {
            if (!isValid()) {
                task.cancel();
                return;
            }
            tick();
        }, 0, Config.TICK_FREQUENCY);
    }

    public void tick() {
        if (System.currentTimeMillis() - lastAttacked < frequency * 50L)
            return;
        if (targets.size() >= maxTargets)
            return;
        List<Mob> mobsInRange = getMobsInRange();
        if (mobsInRange.size() - targets.size() < minTargets)
            return;
        for (int i = 0; i < maxTargets - targets.size(); i++) {
            attack(mobsInRange.get(i));
        }
    }

    /**
     * @param mob the mob to attack
     */
    public void attack(@NotNull Mob mob) {
        if (!isValid())
            return;
        targets.add(mob);
        org.bukkit.entity.Mob bukkitMob = mob.getBukkitMob();
        Location loc = bukkitMob.getLocation();
        Bukkit.getScheduler().runTaskTimer(TowerDefense.instance, task -> {
            if (!isValid() || !mob.isValid() || getSource().distance(loc) > radius) {
                targets.remove(mob);
                task.cancel();
                return;
            }
            if (particleBuilder != null) {
                Vector vector = loc.toVector().subtract(getSource().toVector());
                if (sound != null)
                    getSource().getWorld().playSound(getSource(), sound, 0.5f, 1);
                particleBuilder.clone().offset(vector.getX(), vector.getY(), vector.getZ())
                        .spawn();
            }
            if (damage > bukkitMob.getHealth()) {
                //noinspection DataFlowIssue
                loc.getWorld().playSound(loc, bukkitMob.getDeathSound(), SoundCategory.HOSTILE, 0.5f, 1);
                bukkitMob.remove();
            } else {
                bukkitMob.setHealth(bukkitMob.getHealth() - damage);
            }
            lastAttacked = System.currentTimeMillis();
        }, 0, frequency);
    }

    public @NotNull List<Mob> getMobsInRange() {
        List<Mob> mobs = new ArrayList<>();
        for (LivingEntity entity : getSource().getNearbyLivingEntities(radius)) {
            if (!(entity instanceof org.bukkit.entity.Mob bukkitMob))
                continue;
            Mob mob = Mob.getFrom(bukkitMob);
            if (mob == null)
                continue;
            mobs.add(mob);
        }
        return mobs;
    }

    @Override
    public void placeStructure() {
        getSource().getBlock().setType(Material.ORANGE_STAINED_GLASS);
    }

    public @Nullable Particle particle() {
        if (particleBuilder == null)
            return null;
        return particleBuilder.particle();
    }

    public AggressionBuilding particle(@Nullable Particle particle) {
        if (particle == null) {
            particleBuilder = null;
        } else {
            particleBuilder = new ParticleBuilder(particle)
                    .count(5)
                    .location(getSource())
                    .allPlayers();
        }
        return this;
    }

    public int radius() {
        return radius;
    }

    public AggressionBuilding radius(int radius) {
        this.radius = radius;
        return this;
    }

    public int damage() {
        return damage;
    }

    public AggressionBuilding damage(int damage) {
        this.damage = damage;
        return this;
    }

    public int minTargets() {
        return minTargets;
    }

    public AggressionBuilding minTargets(int minTargets) {
        this.minTargets = minTargets;
        return this;
    }

    public int maxTargets() {
        return maxTargets;
    }

    public AggressionBuilding maxTargets(int maxTargets) {
        this.maxTargets = maxTargets;
        return this;
    }

    /**
     * @return the attack frequency, in ticks
     */
    public int frequency() {
        return frequency;
    }

    /**
     * @param frequency the attack frequency, in ticks
     */
    public AggressionBuilding frequency(int frequency) {
        this.frequency = frequency;
        return this;
    }

    public int maxAmmo() {
        return maxAmmo;
    }

    public AggressionBuilding maxAmmo(int maxAmmo) {
        this.maxAmmo = maxAmmo;
        return this;
    }

    public @Nullable Sound sound() {
        return sound;
    }

    public AggressionBuilding sound(@Nullable Sound sound) {
        this.sound = sound;
        return this;
    }

    /**
     * @return the active targets of this building
     */
    public @NotNull List<Mob> getTargets() {
        return List.copyOf(targets);
    }

}
