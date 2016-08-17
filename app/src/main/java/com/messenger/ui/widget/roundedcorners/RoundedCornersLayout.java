package com.messenger.ui.widget.roundedcorners;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Canvas;
import android.graphics.Rect;
import android.os.Build;
import android.util.AttributeSet;
import android.widget.FrameLayout;

import com.worldventures.dreamtrips.R;

public class RoundedCornersLayout extends FrameLayout {

   private RoundedCornersDelegate roundedCornersDelegate;

   public RoundedCornersLayout(Context context) {
      super(context);
      init(null);
   }

   public RoundedCornersLayout(Context context, AttributeSet attrs) {
      super(context, attrs);
      init(attrs);
   }

   public void init(AttributeSet attrs) {
      int radius = 0;
      if (attrs != null) {
         TypedArray a = getContext().obtainStyledAttributes(attrs, R.styleable.RoundedCornersLayout);
         try {
            if (a.hasValue(R.styleable.RoundedCornersLayout_rcl_radius)) {
               radius = a.getDimensionPixelSize(R.styleable.RoundedCornersLayout_rcl_radius, 0);
            }
         } catch (Exception ex) {
            ex.printStackTrace();
         } finally {
            a.recycle();
         }
      }

      if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
         roundedCornersDelegate = new RoundedCornersDelegateLolipop();
      } else {
         roundedCornersDelegate = new RoundedCornersDelegateSupport();
      }
      roundedCornersDelegate.initialize(this, radius);
   }

   Rect getBoundsRect() {
      return new Rect(0, 0, getWidth(), getHeight());
   }

   @Override
   protected void dispatchDraw(Canvas canvas) {
      roundedCornersDelegate.dispatchDraw(canvas, () -> super.dispatchDraw(canvas));
   }
}
