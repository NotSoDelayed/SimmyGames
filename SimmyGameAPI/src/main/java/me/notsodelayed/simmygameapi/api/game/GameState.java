package me.notsodelayed.simmygameapi.api.game;

import net.md_5.bungee.api.ChatColor;

public enum GameState {

    LOADING("loading", "Loading", ChatColor.BLACK),
    WAITING_FOR_PLAYERS("waiting", "Waiting", ChatColor.GREEN),
    STARTING("starting", "Starting", ChatColor.GOLD),
    INGAME("ingame", "In-Game", ChatColor.RED),
    ENDING("ending", "Ending", ChatColor.DARK_PURPLE),
    DELETED("deleted", "Deleted", ChatColor.BLACK);


    private final String toString, displayName;
    private final ChatColor color;

    GameState(String toString, String displayName, ChatColor color) {
        this.toString = toString;
        this.displayName = displayName;
        this.color = color;
    }

    /**
     * @return the color
     */
    public ChatColor getColor() {
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
