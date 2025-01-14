package me.notsodelayed.towerdefense.building.defense;

import java.util.UUID;

import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import net.kyori.adventure.text.format.TextDecoration;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;

import me.notsodelayed.simmygameapi.lib.fastinv.ItemBuilder;
import me.notsodelayed.towerdefense.building.DirectAggressionBuilding;

public class ArcherTower extends DirectAggressionBuilding {

    public static final ItemStack SPAWNER = new ItemBuilder(Material.OAK_PLANKS)
            .meta(meta -> {
                Component name = Component.text("Archer Tower")
                        .color(NamedTextColor.GOLD)
                        .decorate(TextDecoration.BOLD);
                meta.itemName(name);
            }).build();

    public ArcherTower(Location source, UUID owner, String displayName, int length, int height) {
        super(source, owner, displayName, length, height);
//        this.radius(RADIUS)
//                .damage(DAMAGE)
//                .frequency(FREQUENCY)
//                .maxAmmo(AMMO);
    }

    public static ItemStack spawnerItem() {
        return SPAWNER.clone();
    }

}
