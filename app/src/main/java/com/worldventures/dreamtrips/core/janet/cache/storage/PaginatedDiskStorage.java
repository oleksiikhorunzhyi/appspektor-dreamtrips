package com.worldventures.dreamtrips.core.janet.cache.storage;

import android.support.annotation.Nullable;

import com.worldventures.dreamtrips.core.janet.cache.CacheBundle;

import java.util.ArrayList;
import java.util.List;

import rx.functions.Action1;
import rx.functions.Func0;

public abstract class PaginatedDiskStorage<T> implements PaginatedStorage<List<T>> {

   @Override
   public synchronized void save(@Nullable CacheBundle params, List<T> newData) {
      List<T> dataToSave = new ArrayList<>();
      if (params == null || !params.get(BUNDLE_REFRESH, false)) {
         dataToSave.addAll(getRestoreAction().call());
      }
      dataToSave.addAll(newData);
      getSaveAction().call(dataToSave);
   }

   @Override
   public synchronized List<T> get(@Nullable CacheBundle params) {
      return getRestoreAction().call();
   }

   public abstract Func0<List<T>> getRestoreAction();

   public abstract Action1<List<T>> getSaveAction();
}
