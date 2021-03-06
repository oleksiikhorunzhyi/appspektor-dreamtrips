package com.messenger.ui.util.recyclerview;


import android.graphics.Canvas;
import android.graphics.drawable.Drawable;
import android.support.v7.widget.RecyclerView;
import android.view.View;

public class VerticalDivider extends RecyclerView.ItemDecoration {

   private final Drawable divider;

   public VerticalDivider(Drawable divider) {
      this.divider = divider;
   }

   public void onDrawOver(Canvas c, RecyclerView parent, RecyclerView.State state) {
      int left = parent.getPaddingLeft();
      int right = parent.getWidth() - parent.getPaddingRight();

      int childCount = parent.getChildCount();
      for (int i = 0; i < childCount; i++) {
         View child = parent.getChildAt(i);

         RecyclerView.LayoutParams params = (RecyclerView.LayoutParams) child.getLayoutParams();

         int top = child.getBottom() + params.bottomMargin;
         int bottom = top + divider.getIntrinsicHeight();

         divider.setBounds(left, top, right, bottom);
         divider.draw(c);
      }
   }
}
