package com.messenger.ui.widget;

import android.content.Context;
import android.view.GestureDetector;
import android.view.MotionEvent;
import android.view.View;

public class OnSwipeGestureListener implements View.OnTouchListener {

   private static final int SWIPE_THRESHOLD_DEFAULT = 100;
   private static final int SWIPE_VELOCITY_THRESHOLD_DEFAULT = 100;

   private int swipeThreshold = SWIPE_THRESHOLD_DEFAULT;
   private int swipeVelocityThreshold = SWIPE_VELOCITY_THRESHOLD_DEFAULT;

   private GestureDetector gestureDetector;

   public void setSwipeThreshold(int swipeThreshold) {
      this.swipeThreshold = swipeThreshold;
   }

   public void setSwipeVelocityThreshold(int swipeVelocityThreshold) {
      this.swipeVelocityThreshold = swipeVelocityThreshold;
   }

   public OnSwipeGestureListener(Context context) {
      gestureDetector = new GestureDetector(context, new GestureListener());
   }

   public boolean onTouch(View v, MotionEvent event) {
      return gestureDetector.onTouchEvent(event);
   }

   private final class GestureListener extends GestureDetector.SimpleOnGestureListener {

      @Override
      public boolean onDown(MotionEvent e) {
         return true;
      }

      @Override
      public boolean onSingleTapConfirmed(MotionEvent e) {
         onSingleTap();
         return true;
      }

      @Override
      public boolean onFling(MotionEvent e1, MotionEvent e2, float velocityX, float velocityY) {
         boolean result = false;
         try {
            float diffY = e2.getY() - e1.getY();
            float diffX = e2.getX() - e1.getX();
            if (Math.abs(diffX) > Math.abs(diffY)) {
               if (Math.abs(diffX) > swipeThreshold && Math.abs(velocityX) > swipeVelocityThreshold) {
                  if (diffX > 0) {
                     onSwipeRight();
                  } else {
                     onSwipeLeft();
                  }
               }
               result = true;
            } else if (Math.abs(diffY) > swipeThreshold && Math.abs(velocityY) > swipeVelocityThreshold) {
               if (diffY > 0) {
                  onSwipeBottom();
               } else {
                  onSwipeTop();
               }
            }
            result = true;

         } catch (Exception exception) {
            exception.printStackTrace();
         }
         return result;
      }
   }

   public void onSingleTap() {
   }

   public void onSwipeRight() {
   }

   public void onSwipeLeft() {
   }

   public void onSwipeTop() {
   }

   public void onSwipeBottom() {
   }
}