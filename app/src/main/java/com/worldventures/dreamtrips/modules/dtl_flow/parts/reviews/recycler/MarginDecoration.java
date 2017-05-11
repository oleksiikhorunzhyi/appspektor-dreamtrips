package com.worldventures.dreamtrips.modules.dtl_flow.parts.reviews.recycler;

import android.content.Context;
import android.graphics.Rect;
import android.support.v7.widget.RecyclerView;
import android.view.View;


public class MarginDecoration extends RecyclerView.ItemDecoration {

   private int margin;
   private Context context;

   public MarginDecoration(Context context) {
      this.context = context;
   }

   @Override
   public void getItemOffsets(
         Rect outRect, View view, RecyclerView parent, RecyclerView.State state) {
      outRect.set(margin, margin, margin, margin);
   }
}
