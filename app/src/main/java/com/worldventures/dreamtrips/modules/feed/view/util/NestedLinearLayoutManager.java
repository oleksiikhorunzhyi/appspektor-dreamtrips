package com.worldventures.dreamtrips.modules.feed.view.util;

import android.content.Context;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewTreeObserver;

import timber.log.Timber;

public class NestedLinearLayoutManager extends LinearLayoutManager {

   private int maxHeight = -1;
   private int[] mMeasuredDimension = new int[2];

   public NestedLinearLayoutManager(Context context, int orientation, boolean reverseLayout) {
      super(context, orientation, reverseLayout);
   }

   @Override
   public void onMeasure(RecyclerView.Recycler recycler, RecyclerView.State state, int widthSpec, int heightSpec) {
      if (maxHeight == -1) {
         super.onMeasure(recycler, state, widthSpec, heightSpec);

         return;
      }
      final int widthMode = View.MeasureSpec.getMode(widthSpec);
      final int heightMode = View.MeasureSpec.getMode(heightSpec);
      final int widthSize = View.MeasureSpec.getSize(widthSpec);
      final int heightSize = View.MeasureSpec.getSize(heightSpec);
      int width = 0;
      int height = 0;
      for (int i = 0; i < getItemCount(); i++) {

         if (getOrientation() == HORIZONTAL) {

            measureScrapChild(recycler, i, View.MeasureSpec.makeMeasureSpec(i, View.MeasureSpec.UNSPECIFIED), heightSpec, mMeasuredDimension);

            width = width + mMeasuredDimension[0];
            if (i == 0) {
               height = mMeasuredDimension[1];
            }
         } else {
            measureScrapChild(recycler, i, widthSpec, View.MeasureSpec.makeMeasureSpec(i, View.MeasureSpec.UNSPECIFIED), mMeasuredDimension);
            height = height + mMeasuredDimension[1];
            if (i == 0) {
               width = mMeasuredDimension[0];
            }
         }
      }
      switch (widthMode) {
         case View.MeasureSpec.EXACTLY:
            width = widthSize;
         case View.MeasureSpec.AT_MOST:
         case View.MeasureSpec.UNSPECIFIED:
      }

      switch (heightMode) {
         case View.MeasureSpec.EXACTLY:
            height = heightSize;
         case View.MeasureSpec.AT_MOST:
         case View.MeasureSpec.UNSPECIFIED:
      }

      setMeasuredDimension(width, Math.min(height, maxHeight));
   }


   @Override
   public void onItemsChanged(RecyclerView recyclerView) {
      if (maxHeight == -1) {
         final ViewTreeObserver.OnPreDrawListener preDrawListener = new ViewTreeObserver.OnPreDrawListener() {
            @Override
            public boolean onPreDraw() {
               recyclerView.getViewTreeObserver().removeOnPreDrawListener(this);
               maxHeight = recyclerView.getMeasuredHeight();
               requestLayout();
               return true;
            }
         };
         recyclerView.getViewTreeObserver().addOnPreDrawListener(preDrawListener);
      }
   }


   private void measureScrapChild(RecyclerView.Recycler recycler, int position, int widthSpec, int heightSpec, int[] measuredDimension) {
      try {
         View view = recycler.getViewForPosition(position);
         recycler.bindViewToPosition(view, position);
         if (view != null) {
            RecyclerView.LayoutParams p = (RecyclerView.LayoutParams) view.getLayoutParams();
            int childWidthSpec = ViewGroup.getChildMeasureSpec(widthSpec, getPaddingLeft() + getPaddingRight(), p.width);
            int childHeightSpec = ViewGroup.getChildMeasureSpec(heightSpec, getPaddingTop() + getPaddingBottom(), p.height);
            view.measure(childWidthSpec, childHeightSpec);
            measuredDimension[0] = view.getMeasuredWidth() + p.leftMargin + p.rightMargin;
            measuredDimension[1] = view.getMeasuredHeight() + p.bottomMargin + p.topMargin;
            recycler.recycleView(view);
         }
      } catch (Exception e) {
         Timber.e(e, this.getClass().getSimpleName());
      }
   }

}