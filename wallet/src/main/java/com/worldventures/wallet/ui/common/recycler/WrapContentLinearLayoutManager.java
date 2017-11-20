package com.worldventures.wallet.ui.common.recycler;

import android.content.Context;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;

import timber.log.Timber;

public class WrapContentLinearLayoutManager extends LinearLayoutManager {

   public WrapContentLinearLayoutManager(Context context) {
      super(context);
   }

   @Override
   public void onLayoutChildren(RecyclerView.Recycler recycler, RecyclerView.State state) {
      try {
         super.onLayoutChildren(recycler, state);
      } catch (IndexOutOfBoundsException e) {
         Timber.e("Layout manager failed to process changes");
      }
   }
}
