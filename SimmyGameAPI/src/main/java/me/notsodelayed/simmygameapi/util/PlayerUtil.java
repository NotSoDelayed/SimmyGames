package me.notsodelayed.simmygameapi.util;

import org.bukkit.GameMode;
import org.bukkit.entity.Player;

import me.notsodelayed.simmygameapi.api.GamePlayer;

public class PlayerUtil {

    public static void reset(Player player, GameMode gameMode) {
        if (player == null)
            return;
        player.setGameMode(gameMode);
        player.getInventory().clear();
        player.setLevel(0);
        player.setHealth(20f);
        player.setSaturation(20f);
        player.getActivePotionEffects().forEach(potionEffect -> player.removePotionEffect(potionEffect.getType()));
    }

    public static void reset(GamePlayer gamePlayer, GameMode gameMode) {
        reset(gamePlayer.asBukkitPlayer(), gameMode);
    }

}
