package me.notsodelayed.simmygameapi.api.util;

import org.jetbrains.annotations.Nullable;

public interface Parser<T> {

    @Nullable T parse(String argument);

}
