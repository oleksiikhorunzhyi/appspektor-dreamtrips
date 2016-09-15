package com.techery.spares.ui.fragment.loadable.collection;

import com.techery.spares.adapter.DataListAdapter;
import com.techery.spares.loader.ContentLoader;
import com.techery.spares.loader.ContentLoaderController;
import com.techery.spares.ui.fragment.loadable.LoadableFragment;
import com.techery.spares.ui.view.cell.BaseCell;

public abstract class CollectionFragment<T> extends LoadableFragment<T> {
   protected DataListAdapter<T> dataAdapter;

   @Override
   public void setContentLoader(ContentLoader<T> contentLoader) {
      super.setContentLoader(contentLoader);

      if (getDataAdapter() != null) {
         getDataAdapter().setContentLoader(getContentLoader());
      }

      getContentLoader().load();
   }

   protected ContentLoader<T> createLoaderWithFactory(ContentLoader.LoaderCreator factory) {
      return new ContentLoaderController<T>(getActivity(), getLoaderManager(), factory);
   }

   protected void setConterLoaderFromLoaderCreator(ContentLoader.LoaderCreator creator) {
      setContentLoader(createLoaderWithFactory(creator));
   }

   public DataListAdapter<T> getDataAdapter() {
      return dataAdapter;
   }

   public void setDataAdapter(DataListAdapter<T> dataAdapter) {
      this.dataAdapter = dataAdapter;

      getDataAdapter().setContentLoader(getContentLoader());
   }

   protected abstract BaseCell.CellBuilder getCellBuilder();
}
