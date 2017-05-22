package com.worldventures.dreamtrips.modules.feed.view.util;

import android.os.Bundle;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;

import com.badoo.mobile.util.WeakHandler;
import com.techery.spares.adapter.BaseArrayListAdapter;
import com.worldventures.dreamtrips.R;
import com.worldventures.dreamtrips.modules.feed.view.cell.Focusable;
import com.worldventures.dreamtrips.modules.feed.view.custom.StateRecyclerView;

import butterknife.ButterKnife;
import butterknife.InjectView;

public class StatePaginatedRecyclerViewManager {

   @InjectView(R.id.recyclerView) public StateRecyclerView stateRecyclerView;
   @InjectView(R.id.swipe_container) public SwipeRefreshLayout swipeContainer;

   private WeakHandler weakHandler;
   private PaginationViewManager paginationViewManager;
   private LinearLayoutManager layoutManager;

   public StatePaginatedRecyclerViewManager(View rootView) {
      ButterKnife.inject(this, rootView);
      weakHandler = new WeakHandler();
   }

   public void init(BaseArrayListAdapter adapter, Bundle savedInstanceState) {
      LinearLayoutManager linearLayoutManager = new LinearLayoutManager(stateRecyclerView.getContext(), LinearLayoutManager.VERTICAL, false);
      linearLayoutManager.setAutoMeasureEnabled(true);
      init(adapter, savedInstanceState, linearLayoutManager);
   }

   public void init(BaseArrayListAdapter adapter, Bundle savedInstanceState, LinearLayoutManager linearLayoutManager) {
      layoutManager = linearLayoutManager;
      swipeContainer.setColorSchemeResources(R.color.theme_main_darker);
      layoutManager = new LinearLayoutManager(stateRecyclerView.getContext(), LinearLayoutManager.VERTICAL, false);
      layoutManager.setAutoMeasureEnabled(true);
      stateRecyclerView.setLayoutManager(layoutManager);
      stateRecyclerView.setup(savedInstanceState, adapter);
      paginationViewManager = new PaginationViewManager(stateRecyclerView);
   }


   public void findFirstCompletelyVisibleItemPosition() {
      if (stateRecyclerView.getScrollState() == RecyclerView.SCROLL_STATE_IDLE) {
         float centerPositionY = stateRecyclerView.getY() + stateRecyclerView.getHeight() / 2;

         Focusable focusableViewHolder = findNearestFocusableViewHolder(centerPositionY,
               layoutManager.findFirstVisibleItemPosition(), layoutManager.findLastVisibleItemPosition());
         if (focusableViewHolder != null) focusableViewHolder.onFocused();
      }
   }

   private Focusable findNearestFocusableViewHolder(float centerPositionY, int firstItemPosition,
         int lastItemPosition) {
      Focusable nearestFocusableViewHolder = null;
      float minPositionDelta = Float.MAX_VALUE;

      for (int i = firstItemPosition; i <= lastItemPosition; i++) {
         RecyclerView.ViewHolder viewHolder = stateRecyclerView.findViewHolderForLayoutPosition(i);
         if (viewHolder == null) continue;
         float viewHolderCenterPosition = viewHolder.itemView.getY() + viewHolder.itemView.getHeight() / 2;
         float positionDelta = Math.abs(centerPositionY - viewHolderCenterPosition);

         if (positionDelta < minPositionDelta && viewHolder instanceof Focusable &&
               ((Focusable) viewHolder).canFocus()) {
            minPositionDelta = positionDelta;
            nearestFocusableViewHolder = (Focusable) viewHolder;
         }
      }

      return nearestFocusableViewHolder;
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
         if (swipeContainer != null) swipeContainer.setRefreshing(true);
      });
   }

   public void finishLoading() {
      weakHandler.post(() -> {
         if (swipeContainer != null) swipeContainer.setRefreshing(false);
      });
   }

   public void updateLoadingStatus(boolean loading, boolean noMoreElements) {
      paginationViewManager.updateLoadingStatus(loading, noMoreElements);
   }
}
