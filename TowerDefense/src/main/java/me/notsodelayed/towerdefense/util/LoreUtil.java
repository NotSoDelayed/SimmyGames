package me.notsodelayed.towerdefense.util;

import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;

import me.notsodelayed.simmygameapi.util.StringUtil;
import me.notsodelayed.towerdefense.building.AggressionBuilding;

public class LoreUtil {

    public static Component directRangeLore(int radius, int damage, int frequency, int ammo) {
        return Component.text("\uD83D\uDD31 ").append(Component.text(AggressionBuilding.Type.DIRECT_RANGE.toString()).color(NamedTextColor.GRAY))

                .append(Component.newline())
                .append(Component.text("Radius: "))
                .append(Component.text(radius).color(NamedTextColor.GOLD))

                .append(Component.newline())
                .append(Component.text("Damage: "))
                .append(Component.text(damage).color(NamedTextColor.RED))
                .append(Component.text(damage))
                .append(Component.text(" "))
                .append(Component.text("/ " + StringUtil.decimal(1, frequency * 50 / 1000) + "s").color(NamedTextColor.GRAY))

                .append(Component.newline())
                .append(Component.text("Ammo: "))
                .append(Component.text(ammo).color(NamedTextColor.GOLD));
    }

}
