package me.notsodelayed.simmygameapi.util;

import java.util.Objects;
import java.util.logging.Level;

import me.notsodelayed.simmygameapi.Config;
import me.notsodelayed.simmygameapi.SimmyGameAPI;
import me.notsodelayed.simmygameapi.api.game.Game;

/**
 * Utility class for logging.
 */
public class LoggerUtil {

    public static void debug(String text) {
        SimmyGameAPI.logger.info("[DEBUG] " + text);
    }

    /**
     * Logs a 2-space indented debug message containing both values with {@link Level#INFO} if values match, else {@link Level#WARNING}.
     * @param got the received result
     * @param expected the expected result
     */
    public static void expect(Object got, Object expected) {
        Level logLevel = Objects.equals(expected, got) ? Level.INFO : Level.WARNING;
        SimmyGameAPI.logger.log(logLevel, "  expected " + expected + ", got " + got);
    }

    public static void verbose(Game game, String text) {
        verbose(game, text, Level.INFO, false);
    }

    public static void verbose(Game game, String text, Level logLevel) {
        verbose(game, text, logLevel, false);
    }

    public static void verbose(Game game, String text, boolean force) {
        verbose(game, text, Level.INFO, force);
    }

    public static void verbose(Game game, String text, Level logLevel, boolean force) {
        if (!Config.get(SimmyGameAPI.instance).VERBOSE && !force)
            return;
        SimmyGameAPI.logger.log(logLevel, "[" + game.getClass().getSimpleName() + "-" + game.getDisplayUuid() + "] " + text);
    }

    public static void verbose(String text) {
        verbose(text, Level.INFO, false);
    }

    public static void verbose(String text, Level logLevel) {
        verbose(text, logLevel, false);
    }

    public static void verbose(String text, boolean force) {
        verbose(text, Level.INFO, force);
    }

    public static void verbose(String text, Level logLevel, boolean force) {
        if (!Config.get(SimmyGameAPI.instance).VERBOSE && !force)
            return;
        SimmyGameAPI.logger.log(logLevel, text);
    }

}
