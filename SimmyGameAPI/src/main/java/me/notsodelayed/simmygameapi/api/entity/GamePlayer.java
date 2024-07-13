package me.notsodelayed.simmygameapi.api.entity;

import java.util.Optional;
import java.util.WeakHashMap;

import me.notsodelayed.simmygameapi.api.game.Game;
import me.notsodelayed.simmygameapi.api.kit.GameKit;
import me.notsodelayed.simmygameapi.api.team.GameTeam;
import me.notsodelayed.simmygameapi.util.StringUtil;
import org.bukkit.GameMode;
import org.bukkit.OfflinePlayer;
import org.bukkit.Sound;
import org.bukkit.SoundCategory;
import org.bukkit.World;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

/**
 * Represents a player for a {@link Game}.
 */
public class GamePlayer {

    private static final WeakHashMap<Player, GamePlayer> GAME_PLAYERS = new WeakHashMap<>();

    private final OfflinePlayer player;
    private final Game<GamePlayer> game;
    @Nullable
    private GameKit gameKit;
    @Nullable
    private GameTeam gameTeam;

    public GamePlayer(Player player, Game<GamePlayer> game, @Nullable GameKit gameKit) {
        this.player = player;
        this.game = game;
        this.gameKit = gameKit;
        game.addPlayer(this);
        GAME_PLAYERS.put(player, this);
    }

    /**
     * @param player the player to search
     */
    @Nullable
    public static GamePlayer getFrom(Player player) {
        return GAME_PLAYERS.get(player);
    }

    /**
     * @param messages the messages
     */
    public void message(@NotNull String... messages) {
        Optional.ofNullable(player.getPlayer()).ifPresent(onlinePlayer -> {
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
        Optional.ofNullable(player.getPlayer()).ifPresent(onlinePlayer ->
                onlinePlayer.playSound(onlinePlayer.getLocation(), sound, SoundCategory.RECORDS, volume, pitch)
        );
    }

    /**
     * @return the bukkit player
     */
    public OfflinePlayer asBukkitPlayer() {
        return player;
    }

    /**
     * Applies the current kit to the player.
     * @return whether the operation is successful (i.e. player was alive, was in survival / adventure mode, etc.)
     */
    public boolean applyKit() {
        if (gameKit == null) {
            System.out.println("kit is null");
            return false;
        }
        Player onlinePlayer = player.getPlayer();
        if (onlinePlayer == null) {
            System.out.println("player is null");
            return false;
        }
        GameMode gameMode = onlinePlayer.getGameMode();
        if (!gameMode.equals(GameMode.ADVENTURE) && !gameMode.equals(GameMode.SURVIVAL)) {
            System.out.println("gamemode not adventure nor survival");
            return false;
        }
        gameKit.getItems().forEach((index, item) -> onlinePlayer.getInventory().setItem(index, item));
        onlinePlayer.updateInventory();
        return true;
    }

    /**
     * @return the player associated
     */
    public Player leaveGame() {
        if (!game.getPlayers().contains(this))
            throw new IllegalStateException("GamePlayer#game mismatch");
        return game.removePlayer(this);
    }

    /**
     * Spawns the player into the spawn of game world.
     * @return whether the operation is successful.
     */
    public boolean spawn() {
        Player player = this.asBukkitPlayer().getPlayer();
        if (player == null)
            return false;
        World world = this.getGame().getGameWorld();
        if (world == null)
            return false;
        player.teleport(world.getSpawnLocation());
        return true;
    }

    /**
     * @return the game associated to
     */
    public Game getGame() {
        return game;
    }

    /**
     * @return {@link OfflinePlayer#getName()}
     */
    public String getName() {
        return player.getName();
    }

    /**
     * @return the kit
     */
    @Nullable
    public GameKit getKit() {
        return gameKit;
    }

    /**
     * @param gameKit the kit
     */
    public void setKit(GameKit gameKit) {
        this.gameKit = gameKit;
    }

    /**
     * @return the team
     */
    @Nullable
    public GameTeam getTeam() {
        return gameTeam;
    }

    /**
     * @param gameTeam the team to join
     * @return whether the operation is successful
     * @throws IllegalStateException if provided team does not belong to the current game, or player is already in a team
     */
    public boolean joinTeam(@NotNull GameTeam gameTeam) {
        if (this.gameTeam != null) {
            throw new IllegalStateException(this + " is already in " + (this.gameTeam == gameTeam ? "this" : "a") + " team " + this.gameTeam);
        }
        if (!gameTeam.addPlayer(this))
            return false;
        this.gameTeam = gameTeam;
        return true;
    }

    public boolean leaveTeam() {
        if (this.gameTeam == null)
            return false;
        return this.gameTeam.removePlayer(this);
    }

    @Override
    public String toString() {
        return "GamePlayer{" +
                "player=" + player +
                ", game=" + game +
                ", gameKit=" + gameKit +
                ", gameTeam=" + gameTeam +
                '}';
    }

}
