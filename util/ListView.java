package util;

import java.util.AbstractList;
import java.util.List;

/**
 * Represents an immutable view of a list
 * */
public class ListView<T> extends AbstractList<T> {
    private List<T> peer;

    @Override
    public T get(int index) {
        return peer.get(index);
    }

    @Override
    public int size() {
        return peer.size();
    }

    public ListView(List<T> peer) {
        this.peer = peer;
    }
}