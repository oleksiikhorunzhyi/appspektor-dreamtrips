package com.worldventures.dreamtrips.modules.dtl.view.cell.pagination;

import android.support.annotation.NonNull;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;

public class PaginationManager {

   private boolean loading = false;

   private PaginationListener paginationListener;

   public void setup(@NonNull RecyclerView recyclerView) {
      if (recyclerView.getLayoutManager() instanceof LinearLayoutManager) {
         recyclerView.addOnScrollListener(new RecyclerView.OnScrollListener() {
            @Override
            public void onScrolled(RecyclerView recyclerView, int dx, int dy) {
               int itemCount = recyclerView.getLayoutManager().getItemCount();
               int lastVisibleItemPosition = ((LinearLayoutManager) recyclerView.getLayoutManager()).findLastVisibleItemPosition();
               if (!loading && isRecyclerScrollable(recyclerView) && lastVisibleItemPosition == itemCount - 1 ) {
                  loading = true;
                  if (paginationListener != null) paginationListener.loadNextPage();
               }
            }
         });
      }
   }

   public void updateLoadingStatus(boolean loading) {
      this.loading = loading;
   }

   public void setPaginationListener(PaginationListener paginationListener) {
      this.paginationListener = paginationListener;
   }

   public boolean isRecyclerScrollable(RecyclerView recyclerView) {
      LinearLayoutManager layoutManager = (LinearLayoutManager) recyclerView.getLayoutManager();
      RecyclerView.Adapter adapter = recyclerView.getAdapter();
      return layoutManager.findLastCompletelyVisibleItemPosition() < adapter.getItemCount() - 1;
   }
}
