package com.techery.spares.ui.fragment.loadable;

import com.techery.spares.loader.ContentLoader;
import com.techery.spares.ui.fragment.InjectingFragment;


public abstract class LoadableFragment<T> extends InjectingFragment implements ContentLoader.ContentLoadingObserving<T> {
   private ContentLoader<T> contentLoader;

   @Override
   public void onResume() {
      super.onResume();
      load();
   }

   @Override
   public void onPause() {
      super.onPause();
      getContentLoader().getContentLoaderObserver().unregisterObserver(this);
   }

   private void load() {
      getContentLoader().load();
   }

   protected void reload() {
      getContentLoader().reload();
   }

   public ContentLoader<T> getContentLoader() {
      return contentLoader;
   }

   public void setContentLoader(ContentLoader<T> contentLoader) {
      this.contentLoader = contentLoader;
      getContentLoader().getContentLoaderObserver().registerObserver(this);
   }
}
