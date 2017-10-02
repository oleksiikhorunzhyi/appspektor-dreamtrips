package com.worldventures.dreamtrips.core.janet.cache.storage;

import android.support.annotation.Nullable;

import com.worldventures.core.janet.cache.CacheBundle;
import com.worldventures.core.janet.cache.storage.ClearableStorage;
import com.worldventures.core.janet.cache.storage.PaginatedStorage;

import java.util.ArrayList;
import java.util.List;

public class PaginatedMemoryStorage<T> implements PaginatedStorage<List<T>>, ClearableStorage {

   private List<T> cache = new ArrayList<>();

   @Override
   public synchronized void save(@Nullable CacheBundle params, List<T> data) {
      if (params != null && params.get(BUNDLE_REFRESH, false)) {
         cache.clear();
      }
      cache.addAll(data);
   }

   @Override
   public synchronized List<T> get(@Nullable CacheBundle params) {
      return cache;
   }

   @Override
   public void clearMemory() {
      cache.clear();
   }
}
