package me.notsodelayed.simmygameapi.api;

import java.util.*;
import java.util.function.BiConsumer;
import java.util.function.Supplier;

import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitTask;
import org.jetbrains.annotations.Nullable;

import me.notsodelayed.simmygameapi.SimmyGameAPI;
import me.notsodelayed.simmygameapi.api.game.Game;
import me.notsodelayed.simmygameapi.command.QueueCommandOld;
import me.notsodelayed.simmygameapi.util.StringUtil;

public class Matchmaking {

    public static final Map<Class<? extends Game>, Supplier<? extends Game>> NEW_GAME_CREATOR = new HashMap<>();
    private static final WeakHashMap<Player, Class<? extends Game>> PLAYER_QUEUE = new WeakHashMap<>();
    private static final WeakHashMap<Player, Long> PLAYER_QUEUE_SINCE = new WeakHashMap<>();
    private static final Map<Class<? extends Game>, BiConsumer<Player, Game>> GAME_QUEUE = new HashMap<>();
    private static BukkitTask matchmaking;

    /**
     * Registers a game type into matchmaking.
     * @param gameType the game type
     * @param playerJoinTask the task of joining this game
     */
    public static <T extends Game> void registerGame(Class<T> gameType, BiConsumer<Player, T> playerJoinTask) {
        //noinspection unchecked
        GAME_QUEUE.put(gameType, (BiConsumer<Player, Game>) playerJoinTask);
        // TODO fix this in favor of CAPI
        QueueCommandOld.REGISTERED_GAMES_CACHE = null;
        SimmyGameAPI.logger.info("Registered " + gameType.getSimpleName() + " into matchmaking!");
    }

    public static void unregisterGame(Class<? extends Game> gameType) {
        GAME_QUEUE.remove(gameType);
    }

    public static <T extends Game> void registerGameCreator(Class<T> gameType, Supplier<T> register) {
        NEW_GAME_CREATOR.put(gameType, register);
    }

    /**
     * Creates a new game instance. The game type must register themselves into this beforehand via {@link #registerGameCreator(Class, Supplier)}.
     * @param gameType the game type
     * @return the created game instance, otherwise null if this game type is not supported
     */
    public static <T extends Game> @Nullable T newGame(Class<T> gameType) {
        Supplier<T> register = (Supplier<T>) NEW_GAME_CREATOR.get(gameType);
        if (register == null)
            return null;
        return register.get();
    }

    /**
     * Creates a new game instance. The game type must register themselves into this beforehand via {@link #registerGameCreator(Class, Supplier)}.
     * @param gameType the game type
     * @return the created game instance, otherwise null if this game type is not supported
     */
    public static <T extends Game> @Nullable T newGame(String gameType) {
        Optional<Class<? extends Game>> clazzQuery = NEW_GAME_CREATOR.keySet().stream()
                .filter(clazz -> clazz.getSimpleName().equalsIgnoreCase(gameType))
                .findAny();
        return clazzQuery.map(clazz -> (T) newGame(clazz)).orElse(null);
    }
    /**
     * Queues a player for matchmaking.
     * @param player the player
     * @param gameType the game type to match
     * @return whether the player is successfully queued
     */
    public static boolean queue(Player player, Class<? extends Game> gameType) {
        if (PLAYER_QUEUE.containsKey(player))
            return false;
        if (!GAME_QUEUE.containsKey(gameType))
            return false;
        PLAYER_QUEUE.put(player, gameType);
        PLAYER_QUEUE_SINCE.put(player, System.currentTimeMillis());
        info(player.getName() + " queued for " + gameType.getSimpleName());
        if (matchmaking == null) {
            matchmaking = SimmyGameAPI.scheduler().runTaskTimer(() -> {
                if (PLAYER_QUEUE.isEmpty())
                    return;
                for (Map.Entry<Player, Class<? extends Game>> entry : PLAYER_QUEUE.entrySet()) {
                    Optional<Game> gameQuery = Game.getGames().values().stream()
                            .filter(game -> game.getClass().equals(entry.getValue()))
                            .findAny();
                    Player queuer = entry.getKey();
                    if (gameQuery.isPresent()) {
                        Game game = gameQuery.get();
                        if (!game.isJoinable())
                            continue;
                        queuer.sendMessage(StringUtil.color("&eAttempting to send you to a " + game.getClass().getSimpleName() + " game..."));
                        GAME_QUEUE.get(entry.getValue()).accept(queuer, game);
                        PLAYER_QUEUE.remove(queuer);
                        info(queuer.getName() + " --> " + game.getClass().getSimpleName() + "-" + game.getUuid());
                    }
                    queuer.sendActionBar("[" + Math.floor(System.currentTimeMillis() - PLAYER_QUEUE_SINCE.get(queuer)) + "s]" + "Queueing: " + PLAYER_QUEUE.get(player).getSimpleName());
                }
            }, 0, 20);
        }
        return true;
    }

    public static void dequeue(Player player) {
        PLAYER_QUEUE.remove(player);
        PLAYER_QUEUE_SINCE.remove(player);
        if (PLAYER_QUEUE.keySet().isEmpty() && matchmaking != null) {
            matchmaking.cancel();
            matchmaking = null;
        }
    }

    public static List<String> getRegisteredGames() {
        return GAME_QUEUE.keySet().stream()
                .map(Class::getSimpleName)
                .toList();
    }

    /**
     * @param name the name (case-insensitive)
     * @return the class of game type if registered, otherwise null
     */
    public static @Nullable Class<? extends Game> valueOf(String name) {
        Optional<Class<? extends Game>> query = GAME_QUEUE.keySet().stream()
                .filter(clazz -> clazz.getSimpleName().equalsIgnoreCase(name))
                .findAny();
        return query.orElse(null);
    }

    public static @Nullable <T extends Game> BiConsumer<Player, T> getJoinTask(Class<T> gameType) {
        return (BiConsumer<Player, T>) GAME_QUEUE.get(gameType);
    }

    /**
     * @return whether the matchmaking task is active
     */
    public static boolean isMatching() {
        return matchmaking != null;
    }

    private static void info(String text) {
        SimmyGameAPI.logger.info("[Matchmaking] " + text);
    }

}
