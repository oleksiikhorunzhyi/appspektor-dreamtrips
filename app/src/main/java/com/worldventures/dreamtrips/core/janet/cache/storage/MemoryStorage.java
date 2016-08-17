package com.worldventures.dreamtrips.core.janet.cache.storage;

import android.support.annotation.Nullable;

import com.worldventures.dreamtrips.core.janet.cache.CacheBundle;

public class MemoryStorage<T> implements Storage<T> {
   private volatile T data;

   @Override
   public void save(@Nullable CacheBundle bundle, T data) {
      this.data = data;
   }

   @Override
   public T get(@Nullable CacheBundle bundle) {
      return data;
   }
}