package me.notsodelayed.simmygameapi.api.game;

import net.kyori.adventure.text.format.NamedTextColor;

public enum GameState {

    LOADING("loading", "Loading", NamedTextColor.BLACK),
    WAITING_FOR_PLAYERS("waiting", "Waiting", NamedTextColor.GREEN),
    STARTING("starting", "Starting", NamedTextColor.GOLD),
    INGAME("ingame", "In-Game", NamedTextColor.RED),
    ENDING("ending", "Ending", NamedTextColor.DARK_PURPLE),
    DELETED("deleted", "Deleted", NamedTextColor.BLACK);


    private final String toString, displayName;
    private final NamedTextColor color;

    GameState(String toString, String displayName, NamedTextColor color) {
        this.toString = toString;
        this.displayName = displayName;
        this.color = color;
    }

    /**
     * @return the color
     */
    public NamedTextColor getColor() {
        return color;
    }

    /**
     * @return the string formatted for displaying
     */
    public String getDisplayName() {
        return color + displayName;
    }

    @Override
    public String toString() {
        return toString;
    }

}
