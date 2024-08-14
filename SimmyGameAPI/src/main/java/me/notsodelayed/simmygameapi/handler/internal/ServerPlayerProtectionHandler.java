package me.notsodelayed.simmygameapi.handler.internal;

import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.event.player.PlayerInteractEvent;

import me.notsodelayed.simmygameapi.api.game.player.GamePlayer;
import me.notsodelayed.simmygameapi.util.Util;

public class ServerPlayerProtectionHandler implements Listener {

    // Player interact
    @EventHandler(priority = EventPriority.HIGH, ignoreCancelled = true)
    public void onInteract(PlayerInteractEvent event) {
        Player player = event.getPlayer();
        if (player.getLocation().getWorld().equals(Util.getMainWorld())) {
            event.setCancelled(true);
            return;
        }
        GamePlayer gamePlayer = GamePlayer.getFrom(player);
        if (gamePlayer == null)
            return;
        if (gamePlayer.getGame().hasEnded())
            event.setCancelled(true);
    }

    // Player damage
    @EventHandler(ignoreCancelled = true)
    public void onPlayerDamage(EntityDamageEvent event) {
        if (!(event.getEntity() instanceof Player player))
            return;
        if (player.getLocation().getWorld().equals(Util.getMainWorld())) {
            event.setCancelled(true);
            return;
        }
        GamePlayer gamePlayer = GamePlayer.getFrom(player);
        if (gamePlayer == null)
            return;
        if (gamePlayer.getGame().hasEnded())
            event.setCancelled(true);
    }

}
