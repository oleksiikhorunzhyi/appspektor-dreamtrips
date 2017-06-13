package com.worldventures.dreamtrips.core.janet.cache.storage;

import android.support.annotation.Nullable;

import com.worldventures.dreamtrips.core.janet.cache.CacheBundle;

import java.util.ArrayList;
import java.util.List;

import rx.functions.Action2;
import rx.functions.Func1;

public abstract class KeyValuePaginatedDiskStorage<T> implements Storage<List<T>>, PaginatedStorage<List<T>>,
      KeyValueStorage<List<T>> {

   @Override
   public synchronized void save(@Nullable CacheBundle params, List<T> data) {
      if (params == null || !params.contains(BUNDLE_KEY_VALUE)) {
         throw new IllegalStateException("CacheBundle passed to FifoKeyValueStorage should not be null and should contain BUNDLE_KEY_VALUE");
      }
      String storageKey = params.get(BUNDLE_KEY_VALUE);

      List<T> dataToSave = new ArrayList<>();
      if (!params.get(BUNDLE_REFRESH, false)) {
         dataToSave.addAll(getRestoreFunc().call(storageKey));
      }
      dataToSave.addAll(data);
      getSaveAction().call(storageKey, dataToSave);
   }

   @Override
   public synchronized List<T> get(@Nullable CacheBundle params) {
      return getRestoreFunc().call(params.get(BUNDLE_KEY_VALUE));
   }

   public abstract Action2<String, List<T>> getSaveAction();

   public abstract Func1<String, List<T>> getRestoreFunc();
}
