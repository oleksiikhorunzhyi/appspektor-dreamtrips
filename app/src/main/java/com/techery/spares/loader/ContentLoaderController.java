package com.techery.spares.loader;

import android.content.Context;
import android.os.Bundle;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.Loader;

public class ContentLoaderController<T> implements ContentLoader<T> {
   private static final int DEFAULT_LOADER = 1;

   private T result;

   private ContentLoaderObserver<T> contentLoaderObserver;
   private LoaderManager loaderManager;
   private final Context context;
   private final int loaderID;
   private final LoaderCreator loaderCreator;

   private LoaderManager.LoaderCallbacks<T> loaderCallbacks = new LoaderManager.LoaderCallbacks<T>() {

      @Override
      public final Loader<T> onCreateLoader(int i, Bundle bundle) {
         return ContentLoaderController.this.loaderCreator.createLoader(context, bundle);
      }

      @Override
      public final void onLoadFinished(Loader<T> objectLoader, T o) {
         if (o != null) {
            ContentLoaderController.this.result = o;
            getContentLoaderObserver().sendOnFinishLoading(o);
         } else {
            getContentLoaderObserver().sendOnError(null);
         }
      }

      @Override
      public final void onLoaderReset(Loader<T> objectLoader) {
         objectLoader.reset();
      }
   };

   public ContentLoaderController(Context context, LoaderManager loaderManager, LoaderCreator factory) {
      this(context, loaderManager, DEFAULT_LOADER, factory);
   }

   public ContentLoaderController(Context context, LoaderManager loaderManager, int loaderID, LoaderCreator factory) {
      this.loaderManager = loaderManager;
      this.context = context;
      this.loaderID = loaderID;
      this.loaderCreator = factory;
      this.contentLoaderObserver = new ContentLoaderObserver<>();
   }

   public final void load() {
      getContentLoaderObserver().sendOnStartLoading();
      this.loaderManager.initLoader(this.loaderID, null, loaderCallbacks);
   }

   public final void reload() {
      getContentLoaderObserver().sendOnStartLoading();
      this.loaderManager.restartLoader(this.loaderID, null, loaderCallbacks);
   }

   @Override
   public T getResult() {
      return this.result;
   }

   public ContentLoaderObserver<T> getContentLoaderObserver() {
      return contentLoaderObserver;
   }

}
