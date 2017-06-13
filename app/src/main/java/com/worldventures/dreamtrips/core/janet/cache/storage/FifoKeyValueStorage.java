package com.worldventures.dreamtrips.core.janet.cache.storage;

import android.support.annotation.Nullable;

import com.worldventures.dreamtrips.core.janet.cache.CacheBundle;

import java.util.LinkedHashMap;
import java.util.Map;

public class FifoKeyValueStorage<K, T> implements Storage<T>, KeyValueStorage<T>, ClearableStorage {

   private static final int SIZE_DEFAULT = 3;

   private Map<K, T> map = new LinkedHashMap<K, T>();
   private int maxSize = SIZE_DEFAULT;

   @Override
   public synchronized void save(@Nullable CacheBundle params, T data) {
      if (params == null || !params.contains(BUNDLE_KEY_VALUE)) {
         throw new IllegalStateException("CacheBundle passed to FifoKeyValueStorage should not be null and should contain BUNDLE_KEY_VALUE");
      }
      map.put(params.get(BUNDLE_KEY_VALUE), data);
      if (map.size() > maxSize) {
         K key = map.keySet().iterator().next();
         map.remove(key);
      }
   }

   @Override
   public synchronized T get(@Nullable CacheBundle params) {
      return map.get(params.get(BUNDLE_KEY_VALUE));
   }

   @Override
   public synchronized void clearMemory() {
      map.clear();
   }

   public void setMaxSize(int maxMapSize) {
      this.maxSize = maxMapSize;
   }

   public int getSize() {
      return map.size();
   }
}
