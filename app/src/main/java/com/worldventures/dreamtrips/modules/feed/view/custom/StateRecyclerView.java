package com.worldventures.dreamtrips.modules.feed.view.custom;

import android.content.Context;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.AttributeSet;

import com.techery.spares.adapter.BaseArrayListAdapter;
import com.techery.spares.ui.recycler.RecyclerViewStateDelegate;
import com.worldventures.dreamtrips.modules.common.view.custom.EmptyRecyclerView;
import com.worldventures.dreamtrips.modules.feed.model.FeedItem;

public class StateRecyclerView extends EmptyRecyclerView {

   private BaseArrayListAdapter adapter;

   private RecyclerViewStateDelegate stateDelegate;
   private OffsetYListener offsetYListener;
   private LinearLayoutManager layoutManager;

   public StateRecyclerView(Context context) {
      this(context, null);
   }

   public StateRecyclerView(Context context, AttributeSet attrs) {
      this(context, attrs, 0);
   }

   public StateRecyclerView(Context context, AttributeSet attrs, int defStyle) {
      super(context, attrs, defStyle);
   }

   public void onSaveInstanceState(Bundle outState) {
      stateDelegate.saveStateIfNeeded(outState);
   }

   public void setup(Bundle savedInstanceState, BaseArrayListAdapter adapter) {
      this.adapter = adapter;
      setAdapter(this.adapter);
      //
      stateDelegate = new RecyclerViewStateDelegate();
      stateDelegate.onCreate(savedInstanceState);
      stateDelegate.setRecyclerView(this);
      //
      layoutManager = new LinearLayoutManager(getContext(), LinearLayoutManager.VERTICAL, false);
      layoutManager.setAutoMeasureEnabled(true);
      setLayoutManager(layoutManager);
      //
      addOnScrollListener(new RecyclerView.OnScrollListener() {
         @Override
         public void onScrolled(RecyclerView recyclerView, int dx, int dy) {
            if (offsetYListener != null) offsetYListener.onScroll(StateRecyclerView.this.computeVerticalScrollOffset());
         }
      });
   }

   public void restoreStateIfNeeded() {
      stateDelegate.restoreStateIfNeeded();
   }

   @Override
   protected void onDetachedFromWindow() {
      super.onDetachedFromWindow();
      stateDelegate.onDestroyView();
   }

   public BaseArrayListAdapter<FeedItem> getAdapter() {
      return adapter;
   }

   public void setOffsetYListener(OffsetYListener offsetYListener) {
      this.offsetYListener = offsetYListener;
   }

   public float getScrollOffset() {
      return StateRecyclerView.this.computeVerticalScrollOffset();
   }

   public interface OffsetYListener {
      void onScroll(int yOffset);
   }

   @Override
   public LinearLayoutManager getLayoutManager() {
      return layoutManager;
   }
}
