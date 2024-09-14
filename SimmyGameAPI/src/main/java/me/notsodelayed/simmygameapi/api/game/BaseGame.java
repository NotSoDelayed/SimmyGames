package me.notsodelayed.simmygameapi.api.game;

import java.util.Objects;
import java.util.Set;
import java.util.UUID;
import java.util.stream.Collectors;

import org.bukkit.entity.Player;

import me.notsodelayed.simmygameapi.api.game.player.GamePlayer;

public interface BaseGame {

    /**
     * Marks this game as setup complete and open for players to join and wait.
     * @throws IllegalStateException if this game is not in setup state, or has missing prerequisites
     * @implNote For custom prerequisites, developers must override this method and either:
     * <p>- call super before custom implementations</p>
     * <p>- call {@link Game#setGameState(GameState) Game.setGameState({@link GameState#WAITING_FOR_PLAYERS})} after custom implementations.</p>
     */
    void ready() throws IllegalStateException;

    /**
     * Requests for this game to start while verifying its prerequisite (i.e. game requirements)
     * @see Game#init()
     */
    void start();

    /**
     * Called upon game start countdown depletion, executing tasks to trigger the game systems.
     */
    void tick();

    /**
     * @return the participant players
     */
    Set<? extends GamePlayer> getPlayers();

    /**
     * @return the participant players
     */
    default Set<Player> getBukkitPlayers() {
        return getPlayers().stream()
                .map(GamePlayer::getPlayer)
                .filter(Objects::nonNull)
                .collect(Collectors.toUnmodifiableSet());
    }

    /**
     * @return the uuid of this game
     */
    UUID getUuid();
    /**
     * @return the current state of this game
     */
    GameState getGameState();

    /**
     * @return the settings of this game
     */
    GameSettings getSettings();

}
