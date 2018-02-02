package com.worldventures.janet.cache.storage;

import com.worldventures.janet.cache.CacheBundle;

public class MemoryStorage<T> implements Storage<T>, ClearableStorage {

   private volatile T data;

   @Override
   public void save(CacheBundle bundle, T data) {
      this.data = data;
   }

   @Override
   public T get(CacheBundle bundle) {
      return data;
   }

   @Override
   public void clearMemory() {
      data = null; //NOPMD
   }
}
