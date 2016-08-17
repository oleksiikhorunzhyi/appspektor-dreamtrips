package com.techery.spares.ui.fragment.loadable.collection;

import android.content.Context;

import com.techery.spares.loader.BaseListLoader;

import java.util.List;

public abstract class SimpleListFragment<T, LV> extends ArrayAdapterFragment<T, LV> {
   public static class SimpleListLoader<T> extends BaseListLoader<T> {
      public SimpleListLoader(Context context, LoadingTask<List<T>> loadingTask) {
         super(context, loadingTask);
      }
   }
}
