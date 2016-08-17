package com.techery.spares.ui.fragment.loadable.collection;

import android.view.View;
import android.widget.AdapterView;

import com.techery.spares.adapter.DataListAdapter;

public abstract class InjectingListFragment<T, ET, LV> extends CollectionFragment<T> implements AdapterView.OnItemClickListener {
   protected LV listView;

   @Override
   public void afterCreateView(View rootView) {
      super.afterCreateView(rootView);

      setListView(findListView(rootView));
      setupOnItemClickListener(getListView());
   }

   public LV getListView() {
      return listView;
   }

   public void setListView(LV listView) {
      this.listView = listView;
      linkAdapter(this.listView);
   }

   @Override
   public void setDataAdapter(DataListAdapter<T> dataAdapter) {
      super.setDataAdapter(dataAdapter);

      if (getContentLoader() != null) {
         getDataAdapter().setContentLoader(getContentLoader());
      }

      if (getListView() != null) {
         linkAdapter(getListView());
      }
   }

   public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
      ET item = (ET) getDataAdapter().getItem(position);

      getEventBus().post(new DataListAdapter.Events.ItemSelectionEvent<ET>(item));
   }

   protected abstract void setupOnItemClickListener(LV listView);

   protected abstract void linkAdapter(LV listView);

   protected abstract LV findListView(View rootView);

}
