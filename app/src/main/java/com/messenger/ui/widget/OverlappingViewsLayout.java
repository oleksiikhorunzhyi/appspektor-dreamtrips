package com.messenger.ui.widget;

import android.content.Context;
import android.content.res.TypedArray;
import android.util.AttributeSet;
import android.view.View;
import android.view.ViewGroup;

import com.worldventures.dreamtrips.R;

public class OverlappingViewsLayout extends ViewGroup {

   private static final float OVERLAPPING_WIDTH_PART = 0.3f;

   private float overlappingPart;

   public OverlappingViewsLayout(Context context) {
      super(context);
      init(context, null);
   }

   public OverlappingViewsLayout(Context context, AttributeSet attrs) {
      super(context, attrs);
      init(context, attrs);
   }

   private void init(Context context, AttributeSet attrs) {
      TypedArray a = getContext().getTheme().obtainStyledAttributes(attrs, R.styleable.OverlappingViewsLayout, 0, 0);
      try {
         overlappingPart = a.getFloat(R.styleable.OverlappingViewsLayout_ovl_overlap_part, OVERLAPPING_WIDTH_PART);
      } finally {
         a.recycle();
      }
   }

   @Override
   protected void onLayout(boolean changed, int left, int top, int right, int bottom) {
      final int childCount = getChildCount();
      // starting positions
      int childLeftPos = getPaddingLeft();
      int childTopPos = getPaddingTop();

      for (int i = 0; i < childCount; i++) {
         View view = getChildAt(i);
         if (view.getVisibility() != View.GONE) {
            float overlappingChildWidthPart = overlappingPart;
            // first view does not have overlap
            if (i == 0) {
               overlappingChildWidthPart = 0f;
            }
            int childWidth = view.getMeasuredWidth();
            int childHeight = view.getMeasuredHeight();
            int overlappedChildWidth = (int) (childWidth * overlappingChildWidthPart);
            childLeftPos = childLeftPos - overlappedChildWidth;
            view.layout(childLeftPos, childTopPos, childLeftPos + childWidth, childTopPos + childHeight);
            childLeftPos += childWidth;
         }
      }
   }

   @Override
   protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
      int childCount = getChildCount();
      int totalPaddingVertical = getPaddingBottom() + getPaddingTop();
      // start values for height and width
      int totalWidth = getPaddingLeft() + getPaddingRight();
      int totalHeight = totalPaddingVertical;

      totalHeight = 0;
      int visibleViewsCount = 0;
      for (int i = 0; i < childCount; i++) {
         View view = getChildAt(i);
         if (view.getVisibility() != View.GONE) {
            measureChild(view, widthMeasureSpec, heightMeasureSpec);
            visibleViewsCount++;
            totalHeight = Math.max(totalHeight, view.getMeasuredHeight() + totalPaddingVertical);
         }
      }

      for (int i = 0; i < childCount; i++) {
         View view = getChildAt(i);
         if (view.getVisibility() != View.GONE) {
            visibleViewsCount--;
            float notOverlappingWidthPart = 1f - overlappingPart;
            // last view is fully visible
            if (visibleViewsCount == 0) {
               notOverlappingWidthPart = 1f;
            }
            totalWidth += (int) (view.getMeasuredWidth() * notOverlappingWidthPart);
         }
      }

      setMeasuredDimension(totalWidth, totalHeight);
   }
}
