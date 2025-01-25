package me.notsodelayed.simmygameapi.api.feature;

import java.util.HashMap;
import java.util.Map;
import java.util.function.Function;

import org.jetbrains.annotations.Nullable;

import me.notsodelayed.simmygameapi.api.game.MapGame;
import me.notsodelayed.simmygameapi.api.map.GameMap;

/**
 * Represents a preset feature for a {@link MapGame} to utilise.
 * <p>
 * To create a custom one, the class must extend from this class, and then do the following:
 * <p>
 * - declare a private constructor of your feature which calls super constructor with your other prerequisites
 * <p>
 * - register a creator of your feature class using {@link #registerCreator(Class, Function)}
 */
public abstract class Feature {

    private static final Map<Class<? extends Feature>, Function<MapGame<? extends GameMap>, ? extends Feature>> CREATOR = new HashMap<>();
    private final MapGame<? extends GameMap> game;

    protected Feature(MapGame<? extends GameMap> game) {
        this.game = game;
    }


    protected static <T extends Feature> void registerCreator(Class<T> clazz, Function<MapGame<? extends GameMap>, T> creator) {
        if (CREATOR.containsKey(clazz))
            throw new IllegalStateException("attempted to re-register feature creator for " + clazz.getSimpleName());
        CREATOR.put(clazz, creator);
    }

    public static <T extends Feature> @Nullable T newFeature(MapGame<? extends GameMap> game, Class<T> clazz) {
        return (T) CREATOR.get(clazz).apply(game);
    }

    protected MapGame<? extends GameMap> getGame() {
        return game;
    }

}
