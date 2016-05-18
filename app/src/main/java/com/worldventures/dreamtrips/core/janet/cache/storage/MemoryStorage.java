package com.worldventures.dreamtrips.core.janet.cache.storage;

public class MemoryStorage<T> implements Storage<T> {

    private volatile T data;

    @Override
    public void save(T data) {
        this.data = data;
    }

    @Override
    public T get() {
        return data;
    }
}
