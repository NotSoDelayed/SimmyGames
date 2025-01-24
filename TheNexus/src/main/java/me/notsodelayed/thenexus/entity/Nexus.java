package me.notsodelayed.thenexus.entity;

import java.util.HashMap;
import java.util.Map;
import java.util.function.Consumer;

import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.Sound;
import org.bukkit.block.Block;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import me.notsodelayed.simmygameapi.SimmyGameAPI;
import me.notsodelayed.simmygameapi.api.player.TeamPlayer;
import me.notsodelayed.thenexus.TheNexus;
import me.notsodelayed.thenexus.event.NexusDestroyedEvent;
import me.notsodelayed.thenexus.game.NexusPlayer;
import me.notsodelayed.thenexus.game.NexusGame;
import me.notsodelayed.thenexus.map.NexusMap;
import me.notsodelayed.thenexus.team.NexusTeam;

/**
 * Represents a core entity of a team base of a {@link NexusGame}.
 */
public class Nexus {

    private static final Map<Location, Nexus> NEXUSES = new HashMap<>();
    private final NexusGame<?, ?> game;
    private int health, maxHealth;
    private final Location location;
    private @Nullable NexusPlayer lastDamager = null;
    private boolean damageable;

    /**
     * @param health the health
     * @param maxHealth the max health
     * @param damageable whether to allow damage to this nexus by a player
     */
    public Nexus(NexusGame<?, ?> game, Location location, int health, int maxHealth, boolean damageable) {
        if (NEXUSES.containsKey(location)) {
            TheNexus.instance.getLogger().severe(location + " is already bound to a nexus!");
            throw new IllegalStateException("block is already bound to a nexus");
        }
        this.game = game;
        this.location = location;
        this.health = health;
        this.maxHealth = maxHealth;
        this.damageable = damageable;
        NEXUSES.put(location, this);
    }

    /**
     * @param block the block
     * @return the nexus associated, otherwise null
     */
    // TODO this is brokey pls check
    public static @Nullable Nexus get(Block block) {
        return NEXUSES.get(block.getLocation());
    }

    /**
     * Damages the nexus by the provided damager, where the task executes with the new nexus health.
     * @param damager the damager, or null
     * @param task the task to execute
     * @return whether the operation is successful
     * @see #damage()
     * @see #damage(NexusPlayer)
     */
    public boolean damage(@Nullable NexusPlayer damager, Consumer<Integer> task) {
        if (!damageable)
            return false;
        health--;
        lastDamager = damager;
        task.accept(health);
        if (health <= 0) {
            damageable = false;
            location.getBlock().setType(Material.BEDROCK);
            NexusTeam team = game.getNexusTeam(this);
            game.dispatchPrefixedMessage(team.getDisplayName().append(SimmyGameAPI.miniMessage().deserialize("<gold> nexus has been destroyed!")));
            new NexusDestroyedEvent(game, team, this).callEvent();
        }
        return true;
    }

    /**
     * Damages the nexus by the provided damager.
     * @param damager the damager
     * @return whether the operation is successful
     * @see #damage()
     */
    public boolean damage(@NotNull NexusPlayer damager) {
        return damage(damager, newHealth -> {
            // Skill issue if its null
            assert damager.getTeam() != null;
            NexusTeam victimTeam = game.getNexusTeam(this);
            if (victimTeam == damager.getTeam())
                return;
            game.getTeamManager().getTeams().forEach(team -> {
                // TODO verify that team = team actually works otherwise its just stupid
                if (team == victimTeam) {
                    team.dispatchSound(location, Sound.BLOCK_ANVIL_LAND, 2, 1);
                    team.dispatchMessage(SimmyGameAPI.miniMessage().deserialize("<red>Your nexus is under attack by ").append(Component.text(damager.getName(), damager.getTeam().getColor()).append(Component.text("!"))));
                } else {
                    team.dispatchSound(location, Sound.ENTITY_ITEM_BREAK, 2, 2);
                    team.dispatchMessage(Component.text(damager.getName(), damager.getTeam().getColor()).append(Component.text(" has damaged the ", NamedTextColor.WHITE)).append(victimTeam.getDisplayName()).append(Component.text(" nexus!", NamedTextColor.WHITE)));
                }
            });
        });
    }

    /**
     * Damages the nexus naturally.
     * @return whether the operation is successful
     */
    public boolean damage() {
        return damage(null, newHealth -> {
            NexusTeam victimTeam = game.getNexusTeam(this);
            for (TeamPlayer<?> player : victimTeam.getPlayers())
                player.message("<red>Your nexus has lost 1 health!");
        });
    }

    public NexusGame<?, ?> getGame() {
        return game;
    }

    public Location getLocation() {
        return location.clone();
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

    /**
     * @param health the health to add
     * @return the new health
     */
    public int addHealth(int health) {
        this.health += health;
        return this.health;
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
