package com.techery.spares.storage;

import com.google.common.base.Optional;

public interface ObjectStorage<T> {
    Optional<T> get();
    void put(T obj);
    void destroy();
}
