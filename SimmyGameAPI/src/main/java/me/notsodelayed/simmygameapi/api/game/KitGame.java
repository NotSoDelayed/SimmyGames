package me.notsodelayed.simmygameapi.api.game;

import java.util.Set;

import me.notsodelayed.simmygameapi.api.game.kit.GameKit;

public interface KitGame<K extends GameKit> extends BaseGame {

    /**
     * @return an immutable set of available kits in this game
     */
    Set<K> getKits();

}
