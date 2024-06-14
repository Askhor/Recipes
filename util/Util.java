package util;

import sun.misc.Unsafe;

import java.util.HashMap;
import java.util.Map;

public class Util {
    private static final Unsafe unsafe;
    private static final Map<Class<?>, Object> DEFAULT_INSTANCE = new HashMap<>();

    static {
        Unsafe _unsafe = null;
        try {
            var field = Unsafe.class.getDeclaredField("theUnsafe");
            field.setAccessible(true);
            _unsafe = (Unsafe) field.get(null);
        } catch (NoSuchFieldException | IllegalAccessException e) {
            e.printStackTrace();
        }
        unsafe = _unsafe;
    }

    private static <T> T getDefaultInstance(Class<T> c) {
        var instance = DEFAULT_INSTANCE.get(c);
        if (instance == null) {
            try {
                instance = unsafe.allocateInstance(c);
            } catch (InstantiationException e) {
                throw new RuntimeException(e);
            }
            DEFAULT_INSTANCE.put(c, instance);
        }
        return (T) instance;
    }

    public static <T> T stat(Class<T> c) {
        return getDefaultInstance(c);
    }
}