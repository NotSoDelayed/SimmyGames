package me.notsodelayed.towerdefense.mob;

import java.util.UUID;

import com.destroystokyo.paper.entity.Pathfinder;
import net.kyori.adventure.text.Component;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.block.data.type.Stairs;
import org.bukkit.entity.EntityType;
import org.bukkit.inventory.ItemStack;
import org.bukkit.scheduler.BukkitTask;

import me.notsodelayed.simmygameapi.util.StringUtil;
import me.notsodelayed.towerdefense.Config;
import me.notsodelayed.towerdefense.TowerDefense;

/**
 * Represents a mob (usually spawned by an opponent player) to help invade a base.
 */
public abstract class HostileMob extends Mob {

    private BukkitTask ticking;

    protected HostileMob(UUID owner, EntityType type, Location location) {
        super(owner, type, location, mob -> {
            mob.setPersistent(true);
            mob.setAware(false);
            mob.setRemoveWhenFarAway(false);
            mob.getEquipment().setHelmetDropChance(0);
            mob.getEquipment().setHelmet(new ItemStack(Material.RED_STAINED_GLASS), true);
        });
    }

    @Override
    public void tick() {
        if (ticking == null)
            ticking = Bukkit.getScheduler().runTaskTimer(TowerDefense.instance, () -> {
                Pathfinder pathfinder = getBukkitMob().getPathfinder();
                if (pathfinder.hasPath())
                    return;
                Location location = getBukkitMob().getLocation();

                // TODO use yaml for list of pathfind points?
                Block directionBlock = location.toBlockLocation().add(0, -2, 0).getBlock();
                if (!(directionBlock instanceof Stairs stairs))
                    return;
                switch (stairs.getFacing()) {
                    case NORTH -> pathfinder.findPath(location.clone().add(1, 0, 0));
                    case EAST -> pathfinder.findPath(location.clone().add(0, 0, 1));
                    case SOUTH -> pathfinder.findPath(location.clone().add(-1, 0, 0));
                    case WEST -> pathfinder.findPath(location.clone().add(0, 0, -1));
                    default -> getBukkitMob().getWorld().sendMessage(Component.text(StringUtil.getDisplayUuid(getBukkitMob().getUniqueId()) + ": what the fuc- do I do now?"));
                }
            }, 0, Config.TICK_FREQUENCY);
    }

}
