package me.notsodelayed.thenexus.handler.internal;

import org.bukkit.GameMode;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerQuitEvent;

import me.notsodelayed.simmygameapi.api.game.Game;
import me.notsodelayed.simmygameapi.api.game.player.GamePlayer;
import me.notsodelayed.simmygameapi.util.PlayerUtil;

// TODO move to SimmyGameAPI
public class ServerPlayerJoinQuitHandler implements Listener {

    // TODO adapt to config (auto join? manual join?)
    @EventHandler
    public void onJoin(PlayerJoinEvent event) {
        event.setJoinMessage("");
    }

    @EventHandler
    public void onQuit(PlayerQuitEvent event) {
        Player bukkitPlayer = event.getPlayer();
        GamePlayer gamePlayer = GamePlayer.getFrom(bukkitPlayer);
        if (gamePlayer == null)
            return;
        Game game = gamePlayer.getGame();
        if (game == null)
            return;
        gamePlayer.leaveGame();
        PlayerUtil.clean(bukkitPlayer, GameMode.ADVENTURE);
        if (game.isAboutToStart() && !game.hasMinimumPlayers()) {
            game.cancelGameStartTask("Insufficient players to start this game.");
        }
    }

}
