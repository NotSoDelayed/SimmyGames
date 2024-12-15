package me.notsodelayed.simmygameapi.api;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.WeakHashMap;
import java.util.function.BiConsumer;

import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitTask;

import me.notsodelayed.simmygameapi.SimmyGameAPI;
import me.notsodelayed.simmygameapi.api.game.Game;
import me.notsodelayed.simmygameapi.command.QueueCommand;
import me.notsodelayed.simmygameapi.util.StringUtil;

public class Matchmaking {

    private static final WeakHashMap<Player, Class<? extends Game>> PLAYER_QUEUE = new WeakHashMap<>();
    private static final Map<Class<? extends Game>, BiConsumer<Player, Game>> GAME_QUEUE = new HashMap<>();
    private static BukkitTask matchmaking;

    public static <T extends Game> void registerGame(Class<T> gameType, BiConsumer<Player, T> queueTask) {
        //noinspection unchecked
        GAME_QUEUE.put(gameType, (BiConsumer<Player, Game>) queueTask);
        QueueCommand.REGISTERED_GAMES_CACHE = null;
        SimmyGameAPI.logger.info("Registered " + gameType.getSimpleName() + " into matchmaking!");
    }

    public static void unregisterGame(Class<? extends Game> gameType) {
        GAME_QUEUE.remove(gameType);
    }

    /**
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
        info(player.getName() + " queued for " + gameType.getSimpleName());
        if (matchmaking == null) {
            matchmaking = Bukkit.getScheduler().runTaskTimer(SimmyGameAPI.instance, () -> {
                if (PLAYER_QUEUE.isEmpty())
                    return;
                for (Map.Entry<Player, Class<? extends Game>> entry : PLAYER_QUEUE.entrySet()) {
                    Optional<Game> gameQuery = Game.getGames().values().stream()
                            .filter(game -> game.getClass().equals(entry.getValue()))
                            .findAny();
                    if (gameQuery.isPresent()) {
                        Game game = gameQuery.get();
                        if (!game.isJoinable())
                            continue;
                        entry.getKey().sendMessage(StringUtil.color("&eAttempting to send you to a " + game.getClass().getSimpleName() + " game..."));
                        GAME_QUEUE.get(entry.getValue()).accept(entry.getKey(), game);
                        PLAYER_QUEUE.remove(entry.getKey());
                        info(entry.getKey().getName() + " --> " + game.getClass().getSimpleName() + "-" + game.getUuid());
                    }
                }
            }, 0, 20);
        }
        return true;
    }

    public static void dequeue(Player player) {
        PLAYER_QUEUE.remove(player);
        if (matchmaking != null) {
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
     * @return whether the matchmaking task is active
     */
    public static boolean isMatching() {
        return matchmaking != null;
    }

    private static void info(String text) {
        SimmyGameAPI.logger.info("[Matchmaking] " + text);
    }

}
