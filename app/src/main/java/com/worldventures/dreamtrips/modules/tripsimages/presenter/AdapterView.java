package com.worldventures.dreamtrips.modules.tripsimages.presenter;

public interface AdapterView<T> {
    void add(T item);

    void add(int position, T item);

    void clear();

    void replace(int position, T item);

    void remove(int index);
}
