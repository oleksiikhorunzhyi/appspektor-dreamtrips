package com.worldventures.core.ui.util;

import android.os.Bundle;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.SimpleItemAnimator;

import com.badoo.mobile.util.WeakHandler;
import com.worldventures.core.R;
import com.worldventures.core.ui.view.recycler.StateRecyclerView;

public class StatePaginatedRecyclerViewManager {

   private final StateRecyclerView stateRecyclerView;
   private final SwipeRefreshLayout swipeContainer;
   private final WeakHandler weakHandler;

   private PaginationViewManager paginationViewManager;
   private LinearLayoutManager layoutManager;

   public StatePaginatedRecyclerViewManager(StateRecyclerView stateRecyclerView, SwipeRefreshLayout swipeContainer) {
      this.weakHandler = new WeakHandler();
      this.stateRecyclerView = stateRecyclerView;
      this.swipeContainer = swipeContainer;
   }

   public void init(RecyclerView.Adapter adapter, Bundle savedInstanceState) {
      LinearLayoutManager linearLayoutManager = new LinearLayoutManager(stateRecyclerView.getContext(), LinearLayoutManager.VERTICAL, false);
      linearLayoutManager.setAutoMeasureEnabled(true);
      init(adapter, savedInstanceState, linearLayoutManager);
   }

   public void init(RecyclerView.Adapter adapter, Bundle savedInstanceState, LinearLayoutManager linearLayoutManager) {
      layoutManager = linearLayoutManager;
      swipeContainer.setColorSchemeResources(R.color.paginated_recycler_view_swipe_color);

      RecyclerView.ItemAnimator animator = stateRecyclerView.getItemAnimator();
      if (animator instanceof SimpleItemAnimator) {
         ((SimpleItemAnimator) animator).setSupportsChangeAnimations(false);
      }

      stateRecyclerView.setLayoutManager(layoutManager);
      stateRecyclerView.setup(savedInstanceState, adapter);

      paginationViewManager = new PaginationViewManager(stateRecyclerView);
   }

   public boolean isNoMoreElements() {
      return paginationViewManager.isNoMoreElements();
   }

   public void setOnRefreshListener(SwipeRefreshLayout.OnRefreshListener onRefreshListener) {
      swipeContainer.setOnRefreshListener(onRefreshListener);
   }

   public void setPaginationListener(PaginationViewManager.PaginationListener paginationListener) {
      paginationViewManager.setPaginationListener(paginationListener);
   }

   public void setOffsetYListener(StateRecyclerView.OffsetYListener offsetYListener) {
      stateRecyclerView.setOffsetYListener(offsetYListener);
   }

   public void addItemDecoration(RecyclerView.ItemDecoration itemDecoration) {
      stateRecyclerView.addItemDecoration(itemDecoration);
   }

   public void startLoading() {
      weakHandler.post(() -> {
         if (swipeContainer != null) {
            swipeContainer.setRefreshing(true);
         }
      });
   }

   public void finishLoading() {
      weakHandler.post(() -> {
         if (swipeContainer != null) {
            swipeContainer.setRefreshing(false);
         }
      });
   }

   public void updateLoadingStatus(boolean loading, boolean noMoreElements) {
      paginationViewManager.updateLoadingStatus(loading, noMoreElements);
   }

   public StateRecyclerView getStateRecyclerView() {
      return stateRecyclerView;
   }

   public LinearLayoutManager getLayoutManager() {
      return layoutManager;
   }
}
