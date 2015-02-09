package com.worldventures.dreamtrips.presentation;

import java.util.List;

public interface AdapterView<T> {
    void addAll(List<T> items);

    void add(T item);

    void add(int position, T item);

    void clear();

    void replace(int position, T item);

    void remove(int index);
}