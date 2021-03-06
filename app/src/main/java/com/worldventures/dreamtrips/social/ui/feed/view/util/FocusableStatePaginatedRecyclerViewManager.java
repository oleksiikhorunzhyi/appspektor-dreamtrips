package com.worldventures.dreamtrips.social.ui.feed.view.util;

import android.os.Bundle;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;

import com.worldventures.core.ui.util.StatePaginatedRecyclerViewManager;
import com.worldventures.core.ui.view.recycler.StateRecyclerView;
import com.worldventures.dreamtrips.social.ui.feed.view.cell.Focusable;

import java.util.concurrent.TimeUnit;

import rx.Observable;
import rx.Subscription;
import rx.android.schedulers.AndroidSchedulers;
import rx.subjects.PublishSubject;


public class FocusableStatePaginatedRecyclerViewManager extends StatePaginatedRecyclerViewManager {

   private static final int SCROLL_DEBOUNCE_TIMEOUT = 250;
   private final PublishSubject<Integer> scrollStateSubject = PublishSubject.create();

   private Subscription scrollStateAutoplaySubscription;

   private Focusable lastFocusedItem = null;

   public FocusableStatePaginatedRecyclerViewManager(StateRecyclerView stateRecyclerView, SwipeRefreshLayout swipeContainer) {
      super(stateRecyclerView, swipeContainer);
   }

   @Override
   public void init(RecyclerView.Adapter adapter, Bundle savedInstanceState, LinearLayoutManager linearLayoutManager) {
      initScrollStateListener();
      super.init(adapter, savedInstanceState, linearLayoutManager);
   }

   public void findFirstCompletelyVisibleItemPosition() {
      Focusable focusableViewHolder = findNearestFocusableViewHolder();
      if (focusableViewHolder != null) {
         if (focusableViewHolder == lastFocusedItem) {
            return;
         }
         unFocusLastFocusedItem();
         lastFocusedItem = focusableViewHolder;
         focusableViewHolder.onFocused();
      }
   }

   public void unFocusLastFocusedItem() {
      if (lastFocusedItem != null) {
         lastFocusedItem.onUnfocused();
         lastFocusedItem = null;
      }
   }

   private void initScrollStateListener() {
      getStateRecyclerView().addOnScrollListener(new RecyclerView.OnScrollListener() {
         @Override
         public void onScrollStateChanged(RecyclerView recyclerView, int newState) {
            scrollStateSubject.onNext(newState);
         }
      });
   }

   public void startLookingForCompletelyVisibleItem(Observable.Transformer stopper) {
      stopAutoplayVideos();
      scrollStateAutoplaySubscription = scrollStateSubject
            .startWith(getStateRecyclerView().getScrollState())
            .debounce(SCROLL_DEBOUNCE_TIMEOUT, TimeUnit.MILLISECONDS)
            .filter(state -> state == RecyclerView.SCROLL_STATE_IDLE)
            .compose(stopper)
            .observeOn(AndroidSchedulers.mainThread())
            .subscribe(aVoid -> findFirstCompletelyVisibleItemPosition());
   }

   public void stopAutoplayVideos() {
      if (scrollStateAutoplaySubscription != null && !scrollStateAutoplaySubscription.isUnsubscribed()) {
         unFocusLastFocusedItem();
         scrollStateAutoplaySubscription.unsubscribe();
      }
   }

   private Focusable findNearestFocusableViewHolder() {
      float centerPositionY = getStateRecyclerView().getY() + getStateRecyclerView().getHeight() / 2;

      return findNearestFocusableViewHolder(centerPositionY,
            getLayoutManager().findFirstVisibleItemPosition(), getLayoutManager().findLastVisibleItemPosition());
   }

   private Focusable findNearestFocusableViewHolder(float centerPositionY, int firstItemPosition,
         int lastItemPosition) {
      Focusable nearestFocusableViewHolder = null;
      float minPositionDelta = Float.MAX_VALUE;

      for (int i = firstItemPosition; i <= lastItemPosition; i++) {
         RecyclerView.ViewHolder viewHolder = getStateRecyclerView().findViewHolderForLayoutPosition(i);
         if (viewHolder == null) {
            continue;
         }
         float viewHolderCenterPosition = viewHolder.itemView.getY() + viewHolder.itemView.getHeight() / 2;
         float positionDelta = Math.abs(centerPositionY - viewHolderCenterPosition);

         if (positionDelta < minPositionDelta && viewHolder instanceof Focusable
               && ((Focusable) viewHolder).canFocus()) {
            minPositionDelta = positionDelta;
            nearestFocusableViewHolder = (Focusable) viewHolder;
         }
      }

      return nearestFocusableViewHolder;
   }
}
