package com.worldventures.janet.cache;

import io.techery.janet.ActionHolder;

public interface CachedAction<T> {
   T getCacheData();

   void onRestore(ActionHolder holder, T cache);

   CacheOptions getCacheOptions();
}