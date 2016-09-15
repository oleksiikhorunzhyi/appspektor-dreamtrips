package com.techery.spares.loader;

import android.database.Observable;

public class ContentLoaderObserver<T> extends Observable<ContentLoader.ContentLoadingObserving<T>> {
   public void sendOnStartLoading() {
      synchronized (mObservers) {
         for (ContentLoader.ContentLoadingObserving<T> observing : mObservers) {
            observing.onStartLoading();
         }
      }
   }

   public void sendOnFinishLoading(T result) {
      synchronized (mObservers) {
         for (ContentLoader.ContentLoadingObserving<T> observing : mObservers) {
            observing.onFinishLoading(result);
         }
      }
   }

   public void sendOnError(Throwable throwable) {
      synchronized (mObservers) {
         for (ContentLoader.ContentLoadingObserving<T> observing : mObservers) {
            observing.onError(throwable);
         }
      }
   }
}
