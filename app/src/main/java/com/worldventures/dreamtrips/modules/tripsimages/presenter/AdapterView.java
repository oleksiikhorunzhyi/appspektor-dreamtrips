package com.worldventures.dreamtrips.modules.tripsimages.presenter;

import java.util.List;

public interface AdapterView<T> {

    void add(T item);

    void add(int position, T item);

    void addAll(int position, List<? extends T> items);

    void clear();

    void replace(int position, T item);

    void remove(int index);
}
