package com.techery.spares.loader;

import android.content.Context;
import android.os.Bundle;

public interface ContentLoader<T> {

   public interface LoaderCreator {
      public android.support.v4.content.Loader createLoader(final Context context, Bundle bundle);
   }

   public interface ContentLoadingObserving<T> {
      public void onStartLoading();

      public void onFinishLoading(T result);

      public void onError(Throwable throwable);
   }

   public void load();

   public void reload();

   public T getResult();

   public ContentLoaderObserver<T> getContentLoaderObserver();

}
