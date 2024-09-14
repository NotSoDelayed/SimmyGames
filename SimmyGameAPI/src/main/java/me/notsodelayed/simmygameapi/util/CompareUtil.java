package me.notsodelayed.simmygameapi.util;

public class CompareUtil {

    @SafeVarargs
    public static <T> boolean equalsAny(T subject, T... values) {
        for (T value : values) {
            if (subject.equals(value))
                return true;
        }
        return false;
    }

}
