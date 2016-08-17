package com.messenger.ui.adapter.swipe;

import android.support.v7.widget.RecyclerView;

public class SwipeableAdapterManager<A extends RecyclerView.Adapter & SwipeLayoutContainer> {

   private SwipeableWrapperAdapter wrapperAdapter;

   public RecyclerView.Adapter wrapAdapter(A adapter) {
      return wrapperAdapter = new SwipeableWrapperAdapter(adapter);
   }

   public void closeAllItems() {
      wrapperAdapter.closeAllItems();
   }
}
