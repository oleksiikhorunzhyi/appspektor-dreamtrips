package com.worldventures.core.janet.cache.storage;

import android.support.annotation.Nullable;

import com.worldventures.core.janet.cache.CacheBundle;

public interface Storage<T> {
   void save(@Nullable CacheBundle params, T data);

   T get(@Nullable CacheBundle action);
}
