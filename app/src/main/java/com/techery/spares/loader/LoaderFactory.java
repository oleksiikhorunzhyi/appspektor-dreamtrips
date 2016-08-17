package com.techery.spares.loader;


import android.content.Context;
import android.support.v4.app.LoaderManager;

import java.util.List;

public class LoaderFactory {
   private final Context context;
   private final LoaderManager loaderManager;

   public LoaderFactory(Context context, LoaderManager loaderManager) {
      this.context = context;
      this.loaderManager = loaderManager;
   }

   public <T> CollectionController<T> create(BaseSimpleTaskLoader.LoadingTask<List<T>> loadingTask) {
      ContentLoader.LoaderCreator creator = BaseListLoader.buildCreator(loadingTask);
      return new CollectionController<>(this.context, this.loaderManager, creator);
   }

   public <T> CollectionController<T> create(int loaderId, BaseSimpleTaskLoader.LoadingTask<List<T>> loadingTask) {
      ContentLoader.LoaderCreator creator = BaseListLoader.buildCreator(loadingTask);
      return new CollectionController<>(this.context, this.loaderManager, loaderId, creator);
   }
}
