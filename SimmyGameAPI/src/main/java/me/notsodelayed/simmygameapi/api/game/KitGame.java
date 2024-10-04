package me.notsodelayed.simmygameapi.api.game;

import java.util.Set;

import me.notsodelayed.simmygameapi.api.game.kit.GameKit;
import me.notsodelayed.simmygameapi.api.game.kit.GameKitManager;

public interface KitGame<K extends GameKit> extends BaseGame {

    GameKitManager<K> getKitManager();

}
