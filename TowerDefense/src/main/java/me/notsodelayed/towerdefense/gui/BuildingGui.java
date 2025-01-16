package me.notsodelayed.towerdefense.gui;

import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.entity.Player;

import me.notsodelayed.simmygameapi.lib.fastinv.FastInv;
import me.notsodelayed.simmygameapi.lib.fastinv.ItemBuilder;
import me.notsodelayed.towerdefense.building.defense.ArcherTower;

public class BuildingGui extends FastInv {

    public BuildingGui(Player player) {
        super(27, ChatColor.BLACK + "Place buildings:");
        setItem(0, new ItemBuilder(Material.BOW)
                .meta(meta -> {
                    Component name = Component.text("Archer Tower").color(NamedTextColor.GOLD);
                    meta.itemName(name);
//                    meta.lore(List.of(LoreUtil.directRangeLore(ArcherTower.RADIUS, ArcherTower.DAMAGE, ArcherTower.FREQUENCY, ArcherTower.AMMO)));
                }).build(),
                event -> event.getWhoClicked().getInventory().setItem(1, ArcherTower.spawnerItem()));
        open(player);
    }

}
