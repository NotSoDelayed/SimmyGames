package me.notsodelayed.simmygameapi.api.util;

import java.util.HashSet;
import java.util.Set;
import java.util.function.BiConsumer;
import java.util.function.Function;

import org.jetbrains.annotations.Nullable;

import me.notsodelayed.simmygameapi.util.StringParser;

/**
 * Represents a YAML node. Can be used as a standalone node with a value, or a parent node to hold all child nodes.
 */
public class Node<T> {

    private final Set<Node<?>> childs = new HashSet<>();
    private Node<?> parent = null;
    private final String key;
    private Function<String, T> getter;
    private BiConsumer<String, Throwable> exception;
    private T defaultValue;
    private boolean optional = false;

    public Node(String key) {
        this.key = key;
    }

    /**
     * @param clazz the node type
     * @param key the key
     * @return a new {@link Node} which parses the argument via {@link StringParser#parse(Class, String)}
     */
    public static <T> Node<T> simple(Class<T> clazz, String key) {
        return new Node<T>(key)
                .getter(s -> StringParser.parse(clazz, s));
    }

    /**
     * @param nodes the child node(s) of this node
     * @return this instance
     */
    public Node<T> child(Node<?>... nodes) {
        for (Node<?> node : nodes) {
            node.parent = this;
            childs.add(node);
        }
        return this;
    }

    @Nullable
    public T evaluate() {
        try {
            return getter.apply(this.toString());
        } catch (Exception ex) {
            if (exception != null)
                exception.accept(this.toString(), ex);
            return defaultValue;
        }
    }

    /**
     * @param getter the value handler (value = node value)
     * @return this instance
     * @apiNote if the value of this node doesn't require specific handling, you may safely skip this method
     */
    public Node<T> getter(Function<@Nullable String, @Nullable T> getter) {
        this.getter = getter;
        return this;
    }

    /**
     * @param optional whether this node is optional (default: false)
     * @return this instance
     * @see #defaultValue(T)
     */
    public Node<T> optional(boolean optional) {
        this.optional = optional;
        return this;
    }

    /**
     * @param exception the exception handler
     *                  <p>(values = node value, throwable)</p>
     * @return this instance
     */
    public Node<T> exception(BiConsumer<@Nullable String, Throwable> exception) {
        this.exception = exception;
        return this;
    }

    /**
     * @param defaultValue the default value, usually when <b>{@link #isOptional()} == true</b>, or in case {@link #getter(Function)} returns null, or throws a runtime exception
     * @return this instance
     */
    public Node<T> defaultValue(T defaultValue) {
        this.defaultValue = defaultValue;
        return this;
    }

    public T getDefaultValue() {
        return defaultValue;
    }

    public String getKey() {
        return key;
    }

    @Nullable
    public Node<?> getParent() {
        return parent;
    }

    public boolean isOptional() {
        return optional;
    }

    /**
     * @return the string representation of the node path (parent node inclusive)
     */
    @Override
    public String toString() {
        StringBuilder path = new StringBuilder(key);
        Node<?> iterParent = parent;
        while (iterParent != null) {
            path.insert(0, iterParent.getKey() + ".");
            iterParent = iterParent.getParent();
        }
        return path.toString();
    }
}
