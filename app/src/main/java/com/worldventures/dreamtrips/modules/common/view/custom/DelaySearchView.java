package com.worldventures.dreamtrips.modules.common.view.custom;

import android.content.Context;
import android.os.Handler;
import android.os.Message;
import android.support.v7.widget.SearchView;
import android.util.AttributeSet;


public class DelaySearchView extends SearchView {


   private static final int TRIGGER_ONQUERYTEXT_CHANGE = 1;
   private static final int TRIGGER_ONQUERYTEXT_SUBMIT = 2;

   private final long SEARCH_TRIGGER_DELAY_IN_MS = 1000;

   InnerHandler handler = new InnerHandler();

   static class InnerHandler extends Handler {
      private OnQueryTextListener listener;

      @Override
      public void handleMessage(Message msg) {
         if (msg.what == TRIGGER_ONQUERYTEXT_CHANGE) {
            if (listener != null) {
               listener.onQueryTextChange((String) msg.obj);
            }
         }
         if (msg.what == TRIGGER_ONQUERYTEXT_SUBMIT) {
            if (listener != null) listener.onQueryTextSubmit((String) msg.obj);
         }
      }
   }

   private long delayInMillis = SEARCH_TRIGGER_DELAY_IN_MS;

   public DelaySearchView(Context context) {
      super(context);
   }

   public DelaySearchView(Context context, AttributeSet attrs) {
      super(context, attrs);
   }

   public DelaySearchView(Context context, AttributeSet attrs, int defStyleAttr) {
      super(context, attrs, defStyleAttr);
   }


   @Override
   public void setOnQueryTextListener(OnQueryTextListener listener) {
      handler.listener = listener;
      super.setOnQueryTextListener(new OnQueryTextListener() {
         @Override
         public boolean onQueryTextSubmit(String s) {
            handler.removeMessages(TRIGGER_ONQUERYTEXT_SUBMIT);
            Message msg = new Message();
            msg.what = TRIGGER_ONQUERYTEXT_SUBMIT;
            msg.obj = s;
            handler.sendMessageDelayed(msg, delayInMillis);
            return false;
         }

         @Override
         public boolean onQueryTextChange(String s) {
            handler.removeMessages(TRIGGER_ONQUERYTEXT_CHANGE);
            Message msg = new Message();
            msg.what = TRIGGER_ONQUERYTEXT_CHANGE;
            msg.obj = s;
            handler.sendMessageDelayed(msg, delayInMillis);
            return false;
         }
      });
   }

   public void setDelayInMillis(long delayInMillis) {
      this.delayInMillis = delayInMillis;
   }
}
