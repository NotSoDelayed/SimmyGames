package me.notsodelayed.simmygameapi.api.parser;

import java.util.function.Consumer;

public abstract class Node<T> {

    private final String key;
    private Consumer<String> exception;

    public Node(String key) {
        this.key = key;
    }

    public abstract <V> Node<T> check(Consumer<V> check);

    public Node<T> exception(Consumer<String> exception) {
        this.exception = exception;
        return this;
    }

}
