package com.worldventures.dreamtrips.social.ui.feed.view.custom;

import android.content.Context;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.AttributeSet;

import com.worldventures.core.ui.view.custom.EmptyRecyclerView;
import com.worldventures.core.ui.view.recycler.RecyclerViewStateDelegate;

public class StateRecyclerView extends EmptyRecyclerView {

   private RecyclerView.Adapter adapter;

   private RecyclerViewStateDelegate stateDelegate;
   private OffsetYListener offsetYListener;

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

   public void setup(Bundle savedInstanceState, RecyclerView.Adapter adapter) {
      setup(savedInstanceState, adapter, new LinearLayoutManager(getContext(), LinearLayoutManager.VERTICAL, false));
   }

   public void setup(Bundle savedInstanceState, RecyclerView.Adapter adapter, LinearLayoutManager linearLayoutManager) {
      this.adapter = adapter;
      setAdapter(this.adapter);
      //
      stateDelegate = new RecyclerViewStateDelegate();
      stateDelegate.onCreate(savedInstanceState);
      stateDelegate.setRecyclerView(this);
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

   public RecyclerView.Adapter getAdapter() {
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
}
