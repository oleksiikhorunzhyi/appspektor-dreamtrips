package com.messenger.util;

import android.os.Handler;
import android.os.Looper;
import android.view.View;

import com.daimajia.swipe.SwipeLayout;

public class SwipeClickListener implements SwipeLayout.SwipeListener {

   private static final int DELAY_ENABLE_CLICK_LISTENER = 200;

   private final View itemView;
   private final View.OnClickListener listener;
   private final Handler handler = new Handler(Looper.getMainLooper());

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
      //do nothing
   }

   @Override
   public void onStartClose(SwipeLayout layout) {
      //do nothing
   }

   @Override
   public void onClose(SwipeLayout layout) {
      handler.postDelayed(() -> itemView.setOnClickListener(listener), DELAY_ENABLE_CLICK_LISTENER);
   }

   @Override
   public void onUpdate(SwipeLayout layout, int leftOffset, int topOffset) {
      //do nothing
   }

   @Override
   public void onHandRelease(SwipeLayout layout, float xvel, float yvel) {
      //do nothing
   }

}
