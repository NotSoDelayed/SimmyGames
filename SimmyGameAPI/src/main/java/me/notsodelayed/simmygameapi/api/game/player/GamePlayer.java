package me.notsodelayed.simmygameapi.api.game.player;

import java.util.UUID;
import java.util.WeakHashMap;

import com.google.common.base.Preconditions;
import org.bukkit.Sound;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import me.notsodelayed.simmygameapi.api.game.Game;
import me.notsodelayed.simmygameapi.util.StringUtil;

/**
 * Represents a player for a {@link Game}.
 */
public class GamePlayer implements BasePlayer {

    private static final WeakHashMap<Player, GamePlayer> GAME_PLAYERS = new WeakHashMap<>();

    private final UUID uuid;
    @Nullable
    private Game game;

    public GamePlayer(Player player) {
        uuid = player.getUniqueId();
        GAME_PLAYERS.put(player, this);
    }

    /**
     * @param player the player to get
     */
    @Nullable
    public static GamePlayer getFrom(Player player) {
        return GAME_PLAYERS.get(player);
    }

    /**
     * Joins a game with the default join game message.
     * @param game the game to join
     * @throws IllegalStateException if this player is already in a game
     */
    public void joinGame(Game game) throws IllegalStateException {
        joinGame(game, String.format("&e%s has joined! (%s/%s)", getName(), game.getPlayers().size(), game.getSettings().maxPlayers()));
    }

    /**
     * @param game the game to join
     * @param joinMessage the join message to dispatch to the game
     * @throws IllegalStateException if this player is already in a game
     */
    public void joinGame(Game game, @Nullable String joinMessage) throws IllegalStateException {
        Preconditions.checkState(this.game == null, this + " is already in a " + game);
        game.addPlayer(this);
        if (joinMessage != null)
            game.dispatchPrefixedMessage(joinMessage);
        this.game = game;
    }

    /**
     * Leaves the current game with the default leave game message.
     */
    public void leaveGame() {
        if (this.game != null)
            leaveGame(String.format("&e%s has left! (%s/%s)", getName(), game.getPlayers().size(), game.getSettings().maxPlayers()));
    }

    /**
     * Leaves the current game.
     */
    public void leaveGame(@Nullable String leaveMessage) {
        if (this.game == null)
            return;
        this.game.removePlayer(this);
        if (leaveMessage != null)
            game.dispatchPrefixedMessage(leaveMessage);
        this.game = null;
    }

    /**
     * @param messages the messages
     */
    public void message(@NotNull String... messages) {
        getOptionalPlayer().ifPresent(onlinePlayer -> {
            for (String message : messages)
                onlinePlayer.sendMessage(StringUtil.color(message));
        });
    }

    /**
     * @param sound the sound
     * @param volume the volume
     * @param pitch the pitch
     */
    public void playSound(Sound sound, int volume, int pitch) {
        getOptionalPlayer().ifPresent(onlinePlayer ->
                onlinePlayer.playSound(onlinePlayer.getLocation(), sound, volume, pitch)
        );
    }

    /**
     * @return the game this player is in
     */
    @Nullable
    public Game getGame() {
        return game;
    }

    @Override
    public UUID getUuid() {
        return uuid;
    }

    @Override
    public String toString() {
        return "GamePlayer{" +
                "uuid=" + uuid +
                ", game=" + game +
                '}';
    }

}
