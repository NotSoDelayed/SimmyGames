package me.notsodelayed.simmygameapi.api.game;

import me.notsodelayed.simmygameapi.api.BaseGame;
import me.notsodelayed.simmygameapi.api.kit.GameKit;
import me.notsodelayed.simmygameapi.api.kit.GameKitManager;

public interface KitGame<K extends GameKit> extends BaseGame {

    GameKitManager<K> getKitManager();

}
