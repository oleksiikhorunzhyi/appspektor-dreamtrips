package com.worldventures.dreamtrips.core.janet.cache.storage;

import android.support.annotation.Nullable;

import com.worldventures.dreamtrips.core.janet.cache.CacheBundle;

import java.util.ArrayList;
import java.util.List;

import rx.functions.Action1;
import rx.functions.Func0;

public abstract class PaginatedDiskStorage<T> implements PaginatedStorage<List<T>> {
   private List<T> lastRestoredData = new ArrayList<>();

   @Override
   public void save(@Nullable CacheBundle params, List<T> data) {
      if (params.get(BUNDLE_REFRESH, false)) {
         lastRestoredData.clear();
      }
      List<T> dataToSave = new ArrayList<>(lastRestoredData);
      dataToSave.addAll(data);
      getSaveAction().call(dataToSave);
   }

   @Override
   public List<T> get(@Nullable CacheBundle params) {
      return lastRestoredData = getRestoreAction().call();
   }

   public abstract Func0<List<T>> getRestoreAction();

   public abstract Action1<List<T>> getSaveAction();
}
