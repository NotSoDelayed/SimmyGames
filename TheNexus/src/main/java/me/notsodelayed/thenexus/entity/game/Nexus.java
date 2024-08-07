package me.notsodelayed.thenexus.entity.game;

import java.util.function.Consumer;

import me.notsodelayed.thenexus.entity.NexusPlayer;
import me.notsodelayed.thenexus.game.NexusGame;
import org.bukkit.block.Block;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

/**
 * Represents a core entity of a team base of a {@link NexusGame}.
 */
public class Nexus {

    private int health, maxHealth;
    private final Block block;
    @Nullable
    private NexusPlayer lastDamager = null;
    private boolean damageable;

    /**
     * @param health the health
     * @param maxHealth the max health
     * @param damageable whether to allow damage to this nexus by a player
     */
    public Nexus(@NotNull Block block, int health, int maxHealth, boolean damageable) {
        this.block = block;
        this.health = health;
        this.maxHealth = maxHealth;
        this.damageable = damageable;
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

    /**
     * @return the block
     */
    public Block getBlock() {
        return block;
    }

    /**
     * @return the health
     */
    public int getHealth() {
        return health;
    }

    /**
     * @param health the health
     */
    public void setHealth(int health) {
        this.health = health;
    }

    /**
     * @return the max health
     */
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
    @Nullable
    public NexusPlayer getLastDamager() {
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
