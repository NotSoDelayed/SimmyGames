package me.notsodelayed.thenexus.entity.game;

import java.util.HashMap;
import java.util.Map;
import java.util.function.Consumer;

import org.bukkit.block.Block;
import org.jetbrains.annotations.Nullable;

import me.notsodelayed.simmygameapi.util.Position;
import me.notsodelayed.thenexus.TheNexus;
import me.notsodelayed.thenexus.entity.NexusPlayer;
import me.notsodelayed.thenexus.game.NexusGame;

/**
 * Represents a core entity of a team base of a {@link NexusGame}.
 */
public class Nexus {

    private static final Map<Position, Nexus> NEXUSES = new HashMap<>();

    private int health, maxHealth;
    private final Position position;
    private @Nullable NexusPlayer lastDamager = null;
    private boolean damageable;

    /**
     * @param health the health
     * @param maxHealth the max health
     * @param damageable whether to allow damage to this nexus by a player
     */
    public Nexus(Position position, int health, int maxHealth, boolean damageable) {
        if (NEXUSES.containsKey(position)) {
            TheNexus.instance.getLogger().severe(position + " is already bound to a nexus.");
            throw new IllegalStateException("block is already bound to a nexus");
        }
        this.position = position;
        this.health = health;
        this.maxHealth = maxHealth;
        this.damageable = damageable;
        NEXUSES.put(position, this);
    }

    /**
     * @param block the block
     * @return the nexus associated, otherwise null
     */
    public static @Nullable Nexus get(Block block) {
        return NEXUSES.get(block);
    }

    /**
     * Damages the nexus by the provided player, where the default implemented task will be executed.
     * @param player the player, or null
     * @return whether the operation is successful
     * @see #damage(NexusPlayer, Consumer)
     */
    public boolean damage(@Nullable NexusPlayer player) {
        return damage(player, postHealth -> {
            if (player != null) {
                // todo add this damage thing
            }
        });
    }

    /**
     * Damages the nexus by the provided player, where the task executes with the new nexus health.
     * @param player the player, or null
     * @param task the task to execute
     * @return whether the operation is successful
     * @see #damage(NexusPlayer)
     */
    public boolean damage(@Nullable NexusPlayer player, Consumer<Integer> task) {
        if (health > 0) {
            health--;
            lastDamager = player;
            task.accept(health);
            return true;
        }
        return false;
    }

    public Position getPosition() {
        return position;
    }

    public int getHealth() {
        return health;
    }

    /**
     * @param health the health
     */
    public void setHealth(int health) {
        this.health = health;
    }

    public int getMaxHealth() {
        return maxHealth;
    }

    /**
     * @param maxHealth the max health
     */
    public void setMaxHealth(int maxHealth) {
        this.maxHealth = maxHealth;
    }

    /**
     * @return the previous nexus damager, or null if the nexus was damaged without a player
     */
    public @Nullable NexusPlayer getLastDamager() {
        return lastDamager;
    }

    /**
     * @return whether this nexus can be damaged by a player
     * @see #allowDamage(boolean)
     */
    public boolean isDamageable() {
        return damageable;
    }

    /**
     * @param damageable whether to allow damage to this nexus by a player
     */
    public void allowDamage(boolean damageable) {
        this.damageable = damageable;
    }

}
