package com.worldventures.janet.cache.storage;

import com.worldventures.janet.cache.CacheBundle;

import java.util.List;

public abstract class CombinedListStorage<T> implements Storage<List<T>>, ClearableStorage {

   private final Storage<List<T>> memoryStorage;
   private final Storage<List<T>> diskStorage;

   public CombinedListStorage(Storage<List<T>> memoryStorage, Storage<List<T>> diskStorage) {
      this.memoryStorage = memoryStorage;
      this.diskStorage = diskStorage;
   }

   @Override
   public void save(CacheBundle params, List<T> data) {
      memoryStorage.save(params, data);
      diskStorage.save(params, data);
   }

   @Override
   public List<T> get(CacheBundle params) {
      List<T> data = memoryStorage.get(params);
      if (data == null || data.isEmpty()) {
         data = diskStorage.get(params);
      }
      return data;
   }

   @Override
   public void clearMemory() {
      if (memoryStorage instanceof ClearableStorage) {
         ((ClearableStorage) memoryStorage).clearMemory();
      }
   }
}
