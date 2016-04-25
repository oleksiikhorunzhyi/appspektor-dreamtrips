package com.worldventures.dreamtrips.core.janet.cache.storage;

import java.util.List;

public interface Storage<T> {

    void save(List<T> data);

    List<T> get();
}
