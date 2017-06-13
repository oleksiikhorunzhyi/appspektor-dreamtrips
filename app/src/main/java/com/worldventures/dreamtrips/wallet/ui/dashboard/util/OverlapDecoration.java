package com.worldventures.dreamtrips.wallet.ui.dashboard.util;

import android.graphics.Rect;
import android.support.v7.widget.RecyclerView;
import android.view.View;

import com.worldventures.dreamtrips.wallet.ui.dashboard.util.viewholder.CardGroupHeaderHolder;

public class OverlapDecoration extends RecyclerView.ItemDecoration {

   private int vertOverlap = 0;

   public OverlapDecoration(int vertOverlap) {
      if (vertOverlap >= 0) {
         throw new IllegalArgumentException("vertOverlap should be negative");
      }
      this.vertOverlap = vertOverlap;
   }

   private boolean isDecorated(View view, RecyclerView parent, int itemPosition) {
      if (parent.getChildViewHolder(view) instanceof CardGroupHeaderHolder) {
         return false;
      }
      View prevView = parent.getChildAt(itemPosition - 1);
      boolean prevIsHeader = prevView != null && parent.getChildViewHolder(prevView) instanceof CardGroupHeaderHolder;
      return !prevIsHeader;
   }

   @Override
   public void getItemOffsets(Rect outRect, View view, RecyclerView parent, RecyclerView.State state) {
      final int itemPosition = parent.getChildAdapterPosition(view);
      if (itemPosition == RecyclerView.NO_POSITION) {
         return;
      }
      if (!isDecorated(view, parent, itemPosition)) {
         return;
      }
      if (itemPosition > 0) {
         outRect.set(0, vertOverlap, 0, 0);
      }
   }
}