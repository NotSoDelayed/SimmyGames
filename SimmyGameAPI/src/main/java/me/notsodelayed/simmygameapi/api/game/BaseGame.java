package me.notsodelayed.simmygameapi.api.game;

import java.util.Objects;
import java.util.Set;
import java.util.UUID;
import java.util.stream.Collectors;

import com.google.common.base.Preconditions;
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
    default void ready() throws IllegalStateException {
        Preconditions.checkState(getGameState() == GameState.LOADING, "game is not in loading state");
    }

    /**
     * Requests for this game to start.
     * @see Game#hasMetGameRequirements()
     * @see Game#init()
     */
    void start();

    /**
     * Called upon game start countdown depletion, executing tasks to trigger the game systems.
     */
    void tick();

    /**
     * Ends the game.
     * @see Game#delete()
     * @implNote The default implementation for ending a game accounting with their implemented variables. Subclasses may override this for custom implementation.
     */
    void end();

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
