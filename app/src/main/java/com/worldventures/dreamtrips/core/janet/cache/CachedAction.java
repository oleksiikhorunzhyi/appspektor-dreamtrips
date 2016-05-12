package com.worldventures.dreamtrips.core.janet.cache;

import io.techery.janet.ActionHolder;

public interface CachedAction<T> {

    T getData();

    void onRestore(ActionHolder holder, T cache);

    CacheOptions getOptions();
}
