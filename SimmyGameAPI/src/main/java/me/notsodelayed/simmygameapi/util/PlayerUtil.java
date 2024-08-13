package me.notsodelayed.simmygameapi.util;

import me.notsodelayed.simmygameapi.api.game.player.GamePlayer;
import org.bukkit.GameMode;
import org.bukkit.entity.Player;

public class PlayerUtil {

    public static void clean(Player player, GameMode gameMode) {
        if (player == null)
            return;
        player.setGameMode(gameMode);
        player.getActivePotionEffects().forEach(potionEffect -> player.removePotionEffect(potionEffect.getType()));
        player.getInventory().clear();
    }

    public static void clean(GamePlayer gamePlayer, GameMode gameMode) {
        clean(gamePlayer.getPlayer(), gameMode);
    }

}
