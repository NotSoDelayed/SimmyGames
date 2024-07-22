package me.notsodelayed.simmygameapi.api.registry.parser;

import java.util.function.BiConsumer;
import java.util.function.Function;

import org.jetbrains.annotations.Nullable;

/**
 * Represents a node. Can be used as standalone which represents a simple node, or as an abstract node for child classes for a more complex implementation.
 * @param <V>
 */
public class Node<V> {

    private final String key;
    private Function<String, V> getter;
    private BiConsumer<String, Throwable> exception;
    private V defaultValue;
    private boolean optional = false;

    public Node(String key) {
        this.key = key;
    }

    /**
     * @param getter the value handler (value = node value)
     * @return this instance
     * @apiNote if the value of this node doesn't require specific handling, you may safely skip this method
     */
    public Node<V> getter(Function<@Nullable String, @Nullable V> getter) {
        this.getter = getter;
        return this;
    }

    /**
     * @param optional whether this node is optional (default: false)
     * @return this instance
     * @see #defaultValue(V) 
     */
    public Node<V> optional(boolean optional) {
        this.optional = optional;
        return this;
    }

    /**
     * @param exception the exception handler
     *                  <p>(values = node value, throwable)</p>
     * @return this instance
     */
    public Node<V> exception(BiConsumer<@Nullable String, Throwable> exception) {
        this.exception = exception;
        return this;
    }

    /**
     * @param defaultValue the default value, usually when <b>{@link #isOptional()} == true</b>, or in case {@link #getter(Function)} returns null, or throws a runtime exception
     * @return this instance
     */
    public Node<V> defaultValue(V defaultValue) {
        this.defaultValue = defaultValue;
        return this;
    }

    public V getDefaultValue() {
        return defaultValue;
    }

    public String getKey() {
        return key;
    }

    public boolean isOptional() {
        return optional;
    }

}
