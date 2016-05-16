package com.worldventures.dreamtrips.core.janet.cache.storage;

public interface Storage<T> {

    void save(T data);

    T get();
}
