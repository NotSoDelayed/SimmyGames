package me.notsodelayed.simmygameapi.util;

import java.util.concurrent.ThreadLocalRandom;

import org.bukkit.Bukkit;
import org.bukkit.World;

public class Util {

    private static final ThreadLocalRandom RANDOM = ThreadLocalRandom.current();

    /**
     * Compares an object with many.
     * @return whether the subject matches any of the values
     */
    public static boolean equalsAny(Object subject, Object... values) {
        for (Object o : values) {
            if (subject.equals(o))
                return true;
        }
        return false;
    }

    /**
     * @return the main world of the server
     */
    public static World getMainWorld() {
        return Bukkit.getWorlds().get(0);
    }

    public static int getRandomInt(int max) {
        return RANDOM.nextInt(max);
    }

    public static boolean isNumberWithin(int min, int max, int input) {
        return input >= min && input <= max;
    }

    /**
     * @param input the input to parse
     * @param defaultValue the fallback int
     * @return the parsed input, otherwise defaultInt
     */
    public static int parseIntOrDefault(String input, int defaultValue) {
        try {
            defaultValue = Integer.parseInt(input);
        } catch (Exception ignored) {}
        return defaultValue;
    }

    /**
     * @param input the input to parse
     * @param defaultValue the fallback int
     * @return the parsed input, otherwise defaultInt
     */
    public static double parseDoubleOrDefault(String input, double defaultValue) {
        try {
            defaultValue = Double.parseDouble(input);
        } catch (Exception ignored) {}
        return defaultValue;
    }

}
