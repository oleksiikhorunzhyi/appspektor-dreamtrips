package com.worldventures.dreamtrips.core.janet.cache;

import io.techery.janet.ActionHolder;

public interface CachedAction<T> {

    T getCacheData();

    void onRestore(ActionHolder holder, T cache);

    CacheOptions getCacheOptions();
}
