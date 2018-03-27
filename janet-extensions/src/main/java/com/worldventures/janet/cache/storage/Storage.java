package com.worldventures.janet.cache.storage;

import com.worldventures.janet.cache.CacheBundle;

public interface Storage<T> {
   void save(CacheBundle params, T data);

   T get(CacheBundle action);
}
