package com.worldventures.dreamtrips.social.ui.tripsimages.view.util;

import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;

import rx.functions.Action0;

public class GridLayoutManagerPaginationDelegate extends RecyclerView.OnScrollListener {

   private final Action0 paginationAction;
   private final int visibleThreshold;

   public GridLayoutManagerPaginationDelegate(Action0 paginationAction, int visibleThreshold) {
      this.paginationAction = paginationAction;
      this.visibleThreshold = visibleThreshold;
   }

   @Override
   public void onScrolled(RecyclerView recyclerView, int dx, int dy) {
      if (recyclerView.getLayoutManager() instanceof GridLayoutManager) {
         GridLayoutManager layoutManager = (GridLayoutManager) recyclerView.getLayoutManager();
         int visibleCount = recyclerView.getChildCount();
         int totalCount = layoutManager.getItemCount();
         int firstVisibleItemPosition = layoutManager.findFirstVisibleItemPosition();
         if (totalCount - visibleCount <= firstVisibleItemPosition + visibleThreshold) {
            paginationAction.call();
         }
      }
   }
}
