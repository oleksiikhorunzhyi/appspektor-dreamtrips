package com.worldventures.dreamtrips.core.janet.cache;

import java.util.List;

import io.techery.janet.ActionHolder;

public interface CachedAction<T> {

    List<T> getData();

    void onRestore(ActionHolder holder, List<T> cache);

    CacheOptions getOptions();
}
