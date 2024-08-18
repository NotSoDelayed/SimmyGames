package me.notsodelayed.simmygameapi.util;

import java.util.HashMap;
import java.util.Map;

import org.jetbrains.annotations.Nullable;

import me.notsodelayed.simmygameapi.api.util.Parser;

/**
 * Parses {@link String} to a specific object.
 */
public class StringParser {

    private static final Map<Class<?>, Parser<?>> PARSERS = new HashMap<>();

    /**
     * @param type the type to parse to
     * @param argument the argument to parse
     * @return the parsed object, if applicable, else null
     */
    @Nullable
    public static <T> T parse(Class<T> type, String argument) {
        Parser<T> parser = getParser(type);
        if (parser == null)
            throw new UnsupportedOperationException(String.format("no parser for type %s found", type.getSimpleName()));
        try {
            return parser.parse(argument);
        } catch (Exception ignored) {
            return null;
        }

    }

    /**
     * Adds a parser into this class.
     * @param clazz the class of the provided type
     * @param parser the parser for the provided type
     * @throws RuntimeException if there's an existing parser for the provided type
     */
    public static <T> void addParser(Class<T> clazz, Parser<T> parser) {
        if (PARSERS.containsKey(clazz))
            throw new RuntimeException(String.format("parser for type %s already exists", clazz.getSimpleName()));
        PARSERS.put(clazz, parser);
        LoggerUtil.verbose("[Parser] Registered for type: " + clazz.getSimpleName());
    }

    /**
     * @param type the type
     * @return the parser for this type, else null
     */
    @Nullable
    public static <T> Parser<T> getParser(Class<T> type) {
        //noinspection unchecked
        return (Parser<T>) PARSERS.get(type);
    }

    /**
     * @return the immutable map of parsers, bound to parser type as key
     */
    public static Map<Class<?>, Parser<?>> getParsers() {
        return Map.copyOf(PARSERS);
    }

}
