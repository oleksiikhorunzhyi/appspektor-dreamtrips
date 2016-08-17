package com.messenger.ui.widget.inappnotification;

import android.content.Context;
import android.util.AttributeSet;
import android.view.View;

import com.daimajia.swipe.SwipeLayout;
import com.worldventures.dreamtrips.R;

public abstract class BaseInAppNotificationView extends SwipeLayout implements SwipeLayout.SwipeListener {

   protected InAppNotificationViewListener listener;

   public void setListener(InAppNotificationViewListener listener) {
      this.listener = listener;
   }

   public BaseInAppNotificationView(Context context) {
      super(context);
      initialize();
   }

   public BaseInAppNotificationView(Context context, AttributeSet attrs) {
      super(context, attrs);
      initialize();
   }

   public BaseInAppNotificationView(Context context, AttributeSet attrs, int defStyleAttr) {
      super(context, attrs, defStyleAttr);
      initialize();
   }

   protected void initialize() {
      addSwipeListener(this);
      View dummySwipeBottom = findViewById(R.id.in_app_notif_dummy_bottom);
      addDrag(SwipeLayout.DragEdge.Left, dummySwipeBottom);
      addDrag(SwipeLayout.DragEdge.Right, dummySwipeBottom);

      findViewById(R.id.in_app_notif_content).setOnClickListener((v) -> {
         if (listener != null) {
            listener.onClick();
         }
      });

      View closeBtn = findViewById(R.id.in_app_notif_close_btn);
      if (closeBtn != null) {
         closeBtn.setOnClickListener(v -> {
            if (listener != null) {
               listener.onCloseClick();
            }
         });
      }
   }

   ;

   @Override
   public void onStartOpen(SwipeLayout layout) {
   }

   @Override
   public void onOpen(SwipeLayout layout) {
      // Callback is called is open since we are supposed to have some buttons
      // beneath content view which is being swiped. Instead in our scenario
      // this means that the notification view was swiped away and dismissed.
      if (listener != null) {
         listener.onCloseSwipe();
      }
   }

   @Override
   public void onStartClose(SwipeLayout layout) {
   }

   @Override
   public void onClose(SwipeLayout layout) {
   }

   @Override
   public void onUpdate(SwipeLayout layout, int leftOffset, int topOffset) {

   }

   @Override
   public void onHandRelease(SwipeLayout layout, float xvel, float yvel) {

   }
}
