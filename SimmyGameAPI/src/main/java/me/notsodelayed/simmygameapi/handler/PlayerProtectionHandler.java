package me.notsodelayed.simmygameapi.handler;

import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.event.player.PlayerInteractEvent;

import me.notsodelayed.simmygameapi.api.Game;
import me.notsodelayed.simmygameapi.api.GamePlayer;
import me.notsodelayed.simmygameapi.util.Util;

public class PlayerProtectionHandler implements Listener {

    @EventHandler(priority = EventPriority.HIGH, ignoreCancelled = true)
    public void onInteract(PlayerInteractEvent event) {
        Player player = event.getPlayer();
        if (player.getLocation().getWorld().equals(Util.getMainWorld())) {
            event.setCancelled(true);
            return;
        }
        GamePlayer gamePlayer = GamePlayer.get(player);
        if (gamePlayer == null)
            return;
        Game game = gamePlayer.getGame();
        if (game.isAboutToStart() || game.hasEnded())
            event.setCancelled(true);
    }

    @EventHandler(ignoreCancelled = true)
    public void onPlayerDamage(EntityDamageEvent event) {
        if (!(event.getEntity() instanceof Player player))
            return;
        if (player.getLocation().getWorld().equals(Util.getMainWorld())) {
            event.setCancelled(true);
            return;
        }
        GamePlayer gamePlayer = GamePlayer.get(player);
        if (gamePlayer == null)
            return;
        Game game = gamePlayer.getGame();
        if (game == null)
            return;
        if (game.isAboutToStart() || game.hasEnded())
            event.setCancelled(true);
    }

}
