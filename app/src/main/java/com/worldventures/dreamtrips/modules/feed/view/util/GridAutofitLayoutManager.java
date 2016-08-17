package com.worldventures.dreamtrips.modules.feed.view.util;

import android.content.Context;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.TypedValue;

public class GridAutofitLayoutManager extends GridLayoutManager {
   private float mColumnWidth;
   private boolean mColumnWidthChanged = true;

   public GridAutofitLayoutManager(Context context, float columnWidth) {
      super(context, 1);
      setColumnWidth(checkedColumnWidth(context, columnWidth));
   }

   public GridAutofitLayoutManager(Context context, float columnWidth, int orientation, boolean reverseLayout) {
      super(context, 1, orientation, reverseLayout);
      setColumnWidth(checkedColumnWidth(context, columnWidth));
   }

   private float checkedColumnWidth(Context context, float columnWidth) {
      if (columnWidth <= 0) {
         columnWidth = (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 48, context.getResources()
               .getDisplayMetrics());
      }
      return columnWidth;
   }

   public void setColumnWidth(float newColumnWidth) {
      if (newColumnWidth > 0 && newColumnWidth != mColumnWidth) {
         mColumnWidth = newColumnWidth;
         mColumnWidthChanged = true;
      }
   }

   @Override
   public void onLayoutChildren(RecyclerView.Recycler recycler, RecyclerView.State state) {
      if (mColumnWidthChanged && mColumnWidth > 0) {
         int totalSpace;
         if (getOrientation() == VERTICAL) {
            totalSpace = getWidth();
         } else {
            totalSpace = getHeight();
         }
         int spanCount = (int) Math.max(1, totalSpace / mColumnWidth);
         setSpanCount(spanCount);
         mColumnWidthChanged = false;
      }
      super.onLayoutChildren(recycler, state);
   }
}
