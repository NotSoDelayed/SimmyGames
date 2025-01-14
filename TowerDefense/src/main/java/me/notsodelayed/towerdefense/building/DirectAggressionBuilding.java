package me.notsodelayed.towerdefense.building;

import java.util.UUID;

import org.bukkit.Location;
import org.bukkit.Material;

public class DirectAggressionBuilding extends AggressionBuilding {

    public DirectAggressionBuilding(Location source, UUID owner, String displayName, int length, int height) {
        super(source, owner, displayName, length, height);
    }

    @Override
    public void placeStructure() {
        getSource().getBlock().setType(Material.RED_STAINED_GLASS);
    }

}
