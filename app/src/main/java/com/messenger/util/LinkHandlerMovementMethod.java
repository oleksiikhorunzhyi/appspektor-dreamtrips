package com.messenger.util;

import android.text.Selection;
import android.text.Spannable;
import android.text.method.LinkMovementMethod;
import android.view.MotionEvent;
import android.widget.TextView;

public final class LinkHandlerMovementMethod extends LinkMovementMethod {
   private static final int MOVEMENT_TIMEOUT = 650;

   private static class LazyHolder {
      private static final LinkHandlerMovementMethod INSTANCE = new LinkHandlerMovementMethod();
   }

   public static LinkHandlerMovementMethod getInstance() {
      return LazyHolder.INSTANCE;
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
