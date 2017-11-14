package com.worldventures.dreamtrips.modules.dtl_flow.parts.reviews.recycler;

import android.graphics.Rect;
import android.support.v7.widget.RecyclerView;
import android.view.View;

//TODO: Find out why we need this class
public class MarginDecoration extends RecyclerView.ItemDecoration {

   @Override
   public void getItemOffsets(
         Rect outRect, View view, RecyclerView parent, RecyclerView.State state) {
      outRect.set(0, 0, 0, 0);
   }
}
