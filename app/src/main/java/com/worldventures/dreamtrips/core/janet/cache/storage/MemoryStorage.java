package com.worldventures.dreamtrips.core.janet.cache.storage;

import java.util.List;

import static java.util.Collections.emptyList;

public class MemoryStorage<T> implements Storage<T> {

    private List<T> data = emptyList();

    @Override
    public void save(List<T> data) {
        this.data = data;
    }

    @Override
    public List<T> get() {
        return data;
    }
}
