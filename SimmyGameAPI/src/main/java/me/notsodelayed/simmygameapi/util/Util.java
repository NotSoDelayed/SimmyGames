package me.notsodelayed.simmygameapi.util;

import java.util.concurrent.ThreadLocalRandom;

import org.bukkit.Bukkit;
import org.bukkit.World;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;

public class Util {

    private static final ThreadLocalRandom RANDOM = ThreadLocalRandom.current();

    /**
     * @return the main world of the server
     */
    public static World getMainWorld() {
        return Bukkit.getWorlds().getFirst();
    }

    public static int getRandomInt(int max) {
        return RANDOM.nextInt(max);
    }

    public static boolean isNumberWithin(int min, int max, int input) {
        return input >= min && input <= max;
    }

    public static void setItems(Inventory inventory, ItemStack itemStack, int... indices) {
        for (int index : indices)
            inventory.setItem(index, itemStack.clone());
    }

}
