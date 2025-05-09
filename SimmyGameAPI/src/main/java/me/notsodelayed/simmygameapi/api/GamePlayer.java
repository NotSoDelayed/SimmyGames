package me.notsodelayed.simmygameapi.api;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.function.Consumer;

import com.google.common.base.Preconditions;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.TextComponent;
import net.kyori.adventure.text.format.NamedTextColor;
import net.kyori.adventure.title.Title;
import net.kyori.adventure.util.Ticks;
import org.bukkit.Bukkit;
import org.bukkit.GameMode;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.scheduler.BukkitTask;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import me.notsodelayed.simmygameapi.SimmyGameAPI;
import me.notsodelayed.simmygameapi.util.PlayerUtil;

/**
 * Represents a player of a {@link Game}.
 */
public class GamePlayer implements BasePlayer {

    private static final Map<Player, GamePlayer> GAME_PLAYERS = new HashMap<>();
    private final UUID uuid;
    private Game game;
    private BukkitTask respawnTask = null;

    static {
        Bukkit.getPluginManager().registerEvents(new Listener() {
            @EventHandler
            public void onQuit(PlayerQuitEvent event) {
                GamePlayer gamePlayer = GAME_PLAYERS.get(event.getPlayer());
                if (gamePlayer != null)
                    gamePlayer.leaveGame();
            }
        }, SimmyGameAPI.instance);
    }

    /**
     * @see #get(Player)
     */
    public GamePlayer(@NotNull Player player, @NotNull Game game) {
        Preconditions.checkState(get(player) == null, player.getName() + " is already in a game");
        this.uuid = player.getUniqueId();
        this.game = game;
        GAME_PLAYERS.put(player, this);
        game.addPlayer(this);
    }

    /**
     * @param player the player
     * @return the associated instance, otherwise null
     */
    public static @Nullable GamePlayer get(Player player) {
        if (player == null)
            return null;
        GamePlayer gamePlayer = GAME_PLAYERS.get(player);
        if (gamePlayer == null)
            return null;
        // This shouldn't happen
        if (!gamePlayer.isValid()) {
            SimmyGameAPI.logger.severe(player.getName() + " has GamePlayer instance without a game");
            throw new IllegalStateException("valid GamePlayer instance without a game");
        }
        return gamePlayer;
    }

    @Override
    public void leaveGame() {
        if (game == null)
            return;
        game.removePlayer(this);
        GAME_PLAYERS.remove(asBukkitPlayer());
        game = null;
    }

    // TODO refractor this
    public void respawn(int respawnAfter, Consumer<GamePlayer> postRespawn) {
        validate();
        respawnAfter = Math.max(respawnAfter, 0);
        if (respawnAfter == 0) {
            PlayerUtil.reset(this, game.getGameMode());
            postRespawn.accept(this);
            return;
        }
        PlayerUtil.reset(asBukkitPlayer(), GameMode.SPECTATOR);
        AtomicInteger seconds = new AtomicInteger(respawnAfter);
        int fRespawnAfter = respawnAfter;
        SimmyGameAPI.scheduler().runTaskTimer(task -> {
            respawnTask = task;
            asBukkitPlayer();
            if (game == null) {
                respawnTask.cancel();
                return;
            }
            if (seconds.get() > 0) {
                TextComponent dashLeft = Component.text("-".repeat(fRespawnAfter + 1 - seconds.get()))
                        .color(NamedTextColor.GRAY)
                        .append(Component.text("-".repeat(seconds.get() - 1)));
                TextComponent dashRight = Component.text("-".repeat(seconds.get() - 1))
                        .color(NamedTextColor.GRAY)
                        .append(Component.text("-".repeat(fRespawnAfter + 1 - seconds.get())));
                TextComponent subtitle = dashLeft
                        .append(
                                Component.text(" Respawning ")
                                        .color(NamedTextColor.GOLD))
                        .append(dashRight);
                title(Title.title(Component.empty(), subtitle, Title.Times.times(Ticks.duration(0), Ticks.duration(21), Ticks.duration(0))));
            } else {
                respawnTask.cancel();
                PlayerUtil.reset(this, game.getGameMode());
                postRespawn.accept(this);
            }
            seconds.getAndDecrement();
        }, 0, 20);
    }

    /**
     * @return the game this player is in
     */
    public @NotNull Game getGame() {
        validate();
        return game;
    }

    @Override
    public UUID getUuid() {
        return uuid;
    }

    public boolean isRespawning() {
        return respawnTask != null;
    }

    @Override
    public boolean isValid() {
        return game != null;
    }

    /**
     * @throws IllegalStateException if this instance is outdated
     */
    protected void validate() {
        if (game == null) {
            SimmyGameAPI.logger.severe("Attempted to interact with outdated GamePlayer instance of " + getName());
            throw new IllegalStateException("outdated GamePlayer instance");
        }
    }

    @Override
    public String toString() {
        return getName();
    }

}
