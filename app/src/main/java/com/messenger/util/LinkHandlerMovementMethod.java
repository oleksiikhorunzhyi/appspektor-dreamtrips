package com.messenger.util;

import android.text.Selection;
import android.text.Spannable;
import android.text.method.LinkMovementMethod;
import android.view.MotionEvent;
import android.widget.TextView;

public class LinkHandlerMovementMethod extends LinkMovementMethod {
   private static final int MOVEMENT_TIMEOUT = 650;
   private static LinkHandlerMovementMethod sInstance;

   public static LinkHandlerMovementMethod getInstance() {
      if (sInstance == null) sInstance = new LinkHandlerMovementMethod();
      return sInstance;
   }

   private LinkHandlerMovementMethod() {}

   @Override
   public boolean onTouchEvent(TextView widget, Spannable buffer, MotionEvent event) {
      long pressTime = event.getEventTime() - event.getDownTime();
      if (event.getAction() == MotionEvent.ACTION_UP && pressTime >= MOVEMENT_TIMEOUT) {
         Selection.removeSelection(buffer);
         return false;
      }
      return super.onTouchEvent(widget, buffer, event);
   }
}
