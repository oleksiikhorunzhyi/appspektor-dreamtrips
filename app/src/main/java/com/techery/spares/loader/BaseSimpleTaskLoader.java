package com.techery.spares.loader;

import android.content.Context;
import android.os.Bundle;
import android.support.v4.content.Loader;

import java.util.logging.Logger;

public class BaseSimpleTaskLoader<T> extends BaseAbstractLoader<T> {
   private final LoadingTask<T> loadingTask;

   public BaseSimpleTaskLoader(Context context, LoadingTask<T> loadingTask) {
      super(context);
      this.loadingTask = loadingTask;
   }

   public static <T> ContentLoader.LoaderCreator buildCreator(final BaseSimpleTaskLoader.LoadingTask<T> loadingTask) {
      return new ContentLoader.LoaderCreator() {
         @Override
         public Loader createLoader(Context context, Bundle bundle) {
            BaseSimpleTaskLoader<T> tBaseSimpleTaskLoader = new BaseSimpleTaskLoader<>(context, loadingTask);
            tBaseSimpleTaskLoader.setLogger(Logger.getGlobal());
            return tBaseSimpleTaskLoader;
         }
      };
   }

   public interface LoadingTask<T> {
      T call(Context context, Bundle params);
   }

   protected T perform() {
      return this.loadingTask.call(getContext(), getParams());
   }
}