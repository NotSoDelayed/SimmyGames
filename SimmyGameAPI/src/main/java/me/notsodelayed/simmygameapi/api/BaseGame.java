package me.notsodelayed.simmygameapi.api;

import java.util.Set;
import java.util.UUID;
import java.util.stream.Collectors;

import com.google.common.base.Preconditions;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public interface BaseGame {

    /**
     * Marks this game as setup complete and open for players to join and wait.
     * @throws IllegalStateException if this game is not in setup state, or has missing prerequisites
     * @implNote For custom prerequisites, developers must override this method and either:
     * <p>- call super before custom implementations</p>
     */
    default void ready() throws IllegalStateException {
        Preconditions.checkState(getGameState() == GameState.LOADING, "game is not in loading state");
    }

    /**
     * Requests for this game to start.
     * @see Game#hasMetGameRequirements()
     */
    void start();

    /**
     * Ends the game.
     * @implNote The default implementation for ending a game accounting with their implemented variables. Subclasses may override this for custom implementation.
     */
    void end();

    /**
     * Displays game information to the command user (/game info).
     * @param sender the command user
     */
    void showInfo(CommandSender sender);

    /**
     * @return the participant players
     */
    Set<? extends GamePlayer> getPlayers();

    /**
     * @return the participant players
     */
    default Set<Player> getBukkitPlayers() {
        return getPlayers().stream()
                .map(GamePlayer::asBukkitPlayer)
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
