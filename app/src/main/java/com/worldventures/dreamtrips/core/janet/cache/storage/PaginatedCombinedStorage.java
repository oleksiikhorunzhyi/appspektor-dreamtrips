package com.worldventures.dreamtrips.core.janet.cache.storage;

import android.support.annotation.Nullable;

import com.worldventures.dreamtrips.core.janet.cache.CacheBundle;

import java.util.List;

public abstract class PaginatedCombinedStorage<T> implements PaginatedStorage<List<T>> {

   private PaginatedMemoryStorage<T> memoryStorage;
   private PaginatedDiskStorage<T> diskStorage;

   public PaginatedCombinedStorage(PaginatedMemoryStorage<T> memoryStorage, PaginatedDiskStorage<T> diskStorage) {
      this.memoryStorage = memoryStorage;
      this.diskStorage = diskStorage;
   }

   @Override
   public void save(@Nullable CacheBundle params, List<T> data) {
      memoryStorage.save(params, data);
      diskStorage.save(params, data);
   }

   @Override
   public List<T> get(@Nullable CacheBundle params) {
      List<T> data = memoryStorage.get(params);
      if (data == null || data.isEmpty()) {
         data = diskStorage.get(params);
      }
      return data;
   }
}
