package me.notsodelayed.towerdefense.game;

import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

import me.notsodelayed.simmygameapi.api.Game;
import me.notsodelayed.simmygameapi.api.GamePlayer;

public class DefensePlayer extends GamePlayer {

    private int coins;

    public DefensePlayer(@NotNull Player player, @NotNull Game game) {
        super(player, game);
    }

    public int getCoins() {
        return coins;
    }

    public void setCoins(int coins) {
        this.coins = coins;
    }

    public void addCoins(int coins) {
        this.coins += coins;
    }

}
