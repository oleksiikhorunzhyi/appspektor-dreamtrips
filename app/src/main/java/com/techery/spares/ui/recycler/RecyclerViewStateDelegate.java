package com.techery.spares.ui.recycler;

import android.os.Bundle;
import android.os.Parcelable;
import android.support.annotation.Nullable;
import android.support.v7.widget.RecyclerView;

public class RecyclerViewStateDelegate {
   private static final String DEFAULT_KEY = "view.recycler.state";

   private RecyclerView recyclerView;
   private Parcelable savedRecyclerLayoutState;

   private final String key;

   public RecyclerViewStateDelegate() {
      this(DEFAULT_KEY);
   }

   /**
    * Use in case when you have to save state for more than one list inside the bundle (not rewrite by single key)
    *
    * @param key
    */
   public RecyclerViewStateDelegate(String key) {
      this.key = key;
   }

   public void setRecyclerView(RecyclerView recyclerView) {
      this.recyclerView = recyclerView;
   }

   public void onCreate(@Nullable Bundle savedInstanceState) {
      if (savedInstanceState != null) {
         savedRecyclerLayoutState = savedInstanceState.getParcelable(key);
      }
   }

   public void saveStateIfNeeded(Bundle outState) {
      if (savedRecyclerLayoutState == null && recyclerView != null) {
         savedRecyclerLayoutState = recyclerView.getLayoutManager().onSaveInstanceState();
      }
      if (savedRecyclerLayoutState != null) {
         outState.putParcelable(key, savedRecyclerLayoutState);
      }
   }

   public void restoreStateIfNeeded() {
      if (recyclerView == null || recyclerView.getLayoutManager() == null) {
         throw new IllegalStateException("RecyclerView or LayoutManager is not set");
      }
      if (savedRecyclerLayoutState != null) {
         recyclerView.getLayoutManager().onRestoreInstanceState(savedRecyclerLayoutState);
         savedRecyclerLayoutState = null;
      }
   }

   public void onDestroyView() {
      savedRecyclerLayoutState = recyclerView.getLayoutManager().onSaveInstanceState();
      recyclerView = null;
   }
}

