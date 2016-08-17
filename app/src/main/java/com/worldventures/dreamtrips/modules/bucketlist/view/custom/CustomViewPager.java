package com.worldventures.dreamtrips.modules.bucketlist.view.custom;

import android.content.Context;
import android.support.v4.view.ViewPager;
import android.support.v7.widget.RecyclerView;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.View;

public class CustomViewPager extends ViewPager {

   private boolean isPagingEnabled = true;

   public CustomViewPager(Context context) {
      super(context);
   }

   public CustomViewPager(Context context, AttributeSet attrs) {
      super(context, attrs);
   }

   @Override
   public boolean onTouchEvent(MotionEvent event) {
      return this.isPagingEnabled && super.onTouchEvent(event);
   }

   @Override
   public boolean onInterceptTouchEvent(MotionEvent event) {
      return this.isPagingEnabled && super.onInterceptTouchEvent(event);
   }

   public void setPagingEnabled(boolean b) {
      this.isPagingEnabled = b;
   }

   @Override
   protected boolean canScroll(View v, boolean checkV, int dx, int x, int y) {
      if (v instanceof RecyclerView) {
         return true;
      }

      return super.canScroll(v, checkV, dx, x, y);
   }
}