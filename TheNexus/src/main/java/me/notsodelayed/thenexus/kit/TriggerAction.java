package me.notsodelayed.thenexus.kit;

public enum TriggerAction {

    SNEAK("sneak"),
    UNKNOWN("unknown");

    private String toString;

    TriggerAction(String toString) {
        this.toString = toString;
    }

    @Override
    public String toString() {
        return toString;
    }

}
