package com.messenger.util;

import android.os.Handler;
import android.os.Looper;
import android.view.View;

import com.daimajia.swipe.SwipeLayout;

public class SwipeClickListener implements SwipeLayout.SwipeListener {

   private static final int DELAY_ENABLE_CLICK_LISTENER = 200;

   private View itemView;
   private View.OnClickListener listener;
   private Handler handler = new Handler(Looper.getMainLooper());

   public SwipeClickListener(View itemView, View.OnClickListener listener) {
      this.itemView = itemView;
      this.listener = listener;
   }

   @Override
   public void onStartOpen(SwipeLayout layout) {
      handler.removeCallbacksAndMessages(null);
      itemView.setOnClickListener(null);
   }

   @Override
   public void onOpen(SwipeLayout layout) {

   }

   @Override
   public void onStartClose(SwipeLayout layout) {

   }

   @Override
   public void onClose(SwipeLayout layout) {
      handler.postDelayed(() -> itemView.setOnClickListener(listener), DELAY_ENABLE_CLICK_LISTENER);
   }

   @Override
   public void onUpdate(SwipeLayout layout, int leftOffset, int topOffset) {

   }

   @Override
   public void onHandRelease(SwipeLayout layout, float xvel, float yvel) {

   }

}
