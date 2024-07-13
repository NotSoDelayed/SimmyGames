package me.notsodelayed.simmygameapi.util;

import me.notsodelayed.simmygameapi.api.entity.GamePlayer;
import org.bukkit.GameMode;
import org.bukkit.entity.Player;

public class PlayerUtil {

    public static void clean(Player player, GameMode gameMode) {
        if (player == null)
            return;
        player.setGameMode(gameMode);
        player.clearActivePotionEffects();
        player.getInventory().clear();
    }

    public static void clean(GamePlayer gamePlayer, GameMode gameMode) {
        clean(gamePlayer.asBukkitPlayer().getPlayer(), gameMode);
    }

}
