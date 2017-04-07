package com.worldventures.dreamtrips.core.janet.cache.storage;

import android.support.annotation.Nullable;

import com.worldventures.dreamtrips.core.janet.cache.CacheBundle;

import java.util.List;

public abstract class CombinedListStorage<T> implements Storage<List<T>>, ClearableStorage {

   private Storage<List<T>> memoryStorage;
   private Storage<List<T>> diskStorage;

   public CombinedListStorage(Storage<List<T>> memoryStorage, Storage<List<T>> diskStorage) {
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

   @Override
   public void clearMemory() {
      if (memoryStorage instanceof ClearableStorage) {
         ((ClearableStorage)memoryStorage).clearMemory();
      }
   }
}
