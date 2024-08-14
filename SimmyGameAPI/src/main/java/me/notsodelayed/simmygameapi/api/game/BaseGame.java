package me.notsodelayed.simmygameapi.api.game;

import java.util.Objects;
import java.util.Set;
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

    void tick();

    /**
     * @return an immutable set of the players in this game
     */
    Set<? extends GamePlayer> getPlayers();

    /**
     * @return an immutable set of the bukkit players in this game
     */
    default Set<Player> getBukkitPlayers() {
        return getPlayers().stream()
                .map(GamePlayer::getPlayer)
                .filter(Objects::nonNull)
                .collect(Collectors.toUnmodifiableSet());
    }

}
