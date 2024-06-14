package util;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

/**
 * Simply an ArrayList with some extra features
 * */
public class AList<T> extends ArrayList<T> {
    public final List<T> view = new ListView<>(this);

    public AList() {
        super();
    }

    public AList(Collection<T> values) {
        super(values);
    }

    public AList(T... values) {
        this(List.of(values));
    }
}