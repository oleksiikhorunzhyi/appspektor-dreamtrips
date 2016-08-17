package com.worldventures.dreamtrips.modules.bucketlist.view.custom;

import android.content.Context;
import android.content.res.TypedArray;
import android.util.AttributeSet;
import android.widget.FrameLayout;

import com.worldventures.dreamtrips.R;

public class SquareLayout extends FrameLayout {

   private boolean squareWithWidth;

   public SquareLayout(Context context) {
      this(context, null);
   }

   public SquareLayout(Context context, AttributeSet attrs) {
      this(context, attrs, 0);
   }

   public SquareLayout(Context context, AttributeSet attrs, int defStyleAttr) {
      super(context, attrs, defStyleAttr);

      TypedArray a = context.getTheme().obtainStyledAttributes(attrs, R.styleable.SquareLayout, 0, 0);

      try {
         int value = a.getInt(R.styleable.SquareLayout_side_priority, 0);
         squareWithWidth = value != 0;
      } finally {
         a.recycle();
      }
   }

   @Override
   protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
      int measureValue = heightMeasureSpec;

      if (squareWithWidth) {
         measureValue = widthMeasureSpec;
      }

      super.onMeasure(measureValue, measureValue);
   }
}