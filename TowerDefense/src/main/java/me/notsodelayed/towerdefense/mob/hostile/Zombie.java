package me.notsodelayed.towerdefense.mob.hostile;

import java.util.UUID;

import org.bukkit.Location;
import org.bukkit.entity.EntityType;

import me.notsodelayed.towerdefense.mob.HostileMob;

public class Zombie extends HostileMob {

    public Zombie(UUID owner, EntityType type, Location location) {
        super(owner, type, location);
    }


    @Override
    public double factoryHealth() {
        return 10;
    }

}
