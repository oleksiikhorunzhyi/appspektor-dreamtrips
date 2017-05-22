package com.worldventures.dreamtrips.core.janet.cache.storage;

import android.support.annotation.Nullable;

import com.worldventures.dreamtrips.core.janet.cache.CacheBundle;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class KeyValuePaginatedMemoryStorage<T> implements Storage<List<T>>, PaginatedStorage<List<T>>,
      KeyValueStorage<List<T>> {

   private Map<String, List<T>> data = new HashMap<>();

   @Override
   public synchronized void save(@Nullable CacheBundle params, List<T> newData) {
      checkParams(params);
      String storageKey = params.get(BUNDLE_KEY_VALUE);

      List<T> dataToSave = new ArrayList<>();
      if (!params.get(BUNDLE_REFRESH, false)) {
         List<T> existingData = data.get(storageKey);
         if (existingData != null) {
            dataToSave.addAll(existingData);
         }
      }
      dataToSave.addAll(newData);
      data.put(storageKey, dataToSave);
   }

   @Override
   public synchronized List<T> get(@Nullable CacheBundle params) {
      checkParams(params);
      return data.get(params.get(BUNDLE_KEY_VALUE));
   }

   private void checkParams(@Nullable CacheBundle params) {
      if (params == null || !params.contains(BUNDLE_KEY_VALUE)) {
         throw new IllegalStateException("CacheBundle passed to FifoKeyValueStorage should not be null and should contain BUNDLE_KEY_VALUE");
      }
   }
}
