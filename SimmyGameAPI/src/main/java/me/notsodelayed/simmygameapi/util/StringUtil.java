package me.notsodelayed.simmygameapi.util;

import java.util.regex.Matcher;
import java.util.regex.Pattern;
import net.md_5.bungee.api.ChatColor;
import org.bukkit.event.entity.EntityDamageEvent;

public class StringUtil {

    private static final Pattern HEX_PATTERN = Pattern.compile("<#([A-Fa-f\\d]){6}>");

    public static final String CHECK_MARK = "(/)";
    public static final String CROSSED_MARK = "(x)";
    public static final String INFORMATION_MARK = "(i)";

    /**
     * Uses {@link net.md_5.bungee.api.ChatColor} to format the provided text.
     * @param text the text
     * @return the colored text
     */

    public static String color(String text) {
//        Matcher matcher = HEX_PATTERN.matcher(text);
//        while (matcher.find()) {
//            ChatColor hexColor = ChatColor.of(matcher.group().substring(1, matcher.group().length() - 1));
//            String before = text.substring(0, matcher.start());
//            String after = text.substring(matcher.end());
//            text = before + hexColor + after;
//            matcher = HEX_PATTERN.matcher(text);
//        }
        return ChatColor.translateAlternateColorCodes('&', text);
    }

    /**
     * Gets a simple summary of a {@link EntityDamageEvent}.
     * @param event the event
     * @return the summary
     */
    public static String getDamageEventSummary(EntityDamageEvent event) {
        if (event == null)
            return "Died!";
        return switch (event.getCause()) {
            case BLOCK_EXPLOSION, ENTITY_EXPLOSION -> "Kabooom!!";
            case CONTACT, THORNS -> "Sharp!";
//            case CRAMMING -> "Pancaked!";
//            case DRAGON_BREATH -> "Smell like dragon!";
            case DROWNING -> "I can't swim!";
//            case DRYOUT -> "Missing H2O!";
//            case ENTITY_ATTACK, ENTITY_SWEEP_ATTACK -> "Killed!";
            case FALL -> "You ain't a cat!";
            case FALLING_BLOCK -> "Watch the sky!";
            // case HOT_FLOOR
            case FIRE, FIRE_TICK, LAVA -> "Burnt!!";
//            case FLY_INTO_WALL -> "Mayday! Mayday! Mayday!";
//            case FREEZE -> "Chill!";
            case LIGHTNING -> "Zapped!";
            case MAGIC -> "Na^2CO^3!";
            case MELTING -> "Melted!";
            case POISON, WITHER -> "Poisoned!";
            case PROJECTILE -> "Shot!";
//            case SONIC_BOOM -> "Soooooonic BOOOM!!";
            case STARVATION -> "Eat something!";
            case SUFFOCATION -> "Choked!";
            // case WORLD_BORDER
            case VOID -> "Goodbye cruel world...";
            // CUSTOM, KILL, SUICIDE
            default -> "Died!";
        };
    }

    public static boolean containsAny(String subject, String[] values) {
        for (String value : values) {
            if (subject.equals(value))
                return true;
        }
        return false;
    }

}
