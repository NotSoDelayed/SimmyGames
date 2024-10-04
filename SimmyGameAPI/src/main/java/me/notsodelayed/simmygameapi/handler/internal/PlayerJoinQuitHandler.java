package me.notsodelayed.simmygameapi.handler.internal;

import net.kyori.adventure.text.Component;
import org.bukkit.GameMode;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerQuitEvent;

import me.notsodelayed.simmygameapi.api.game.Game;
import me.notsodelayed.simmygameapi.api.game.player.GamePlayer;
import me.notsodelayed.simmygameapi.util.PlayerUtil;

public class PlayerJoinQuitHandler implements Listener {

    // TODO adapt to config (auto join? manual join?)
    @EventHandler
    public void onJoin(PlayerJoinEvent event) {
        event.joinMessage(Component.empty());

    }

    @EventHandler
    public void onQuit(PlayerQuitEvent event) {
        event.quitMessage(Component.empty());
        Player bukkitPlayer = event.getPlayer();
        GamePlayer gamePlayer = GamePlayer.get(bukkitPlayer);
        if (gamePlayer == null)
            return;
        Game game = gamePlayer.getGame();
        if (game == null)
            return;
        gamePlayer.leaveGame();
        PlayerUtil.clean(bukkitPlayer, GameMode.ADVENTURE);
    }

}
