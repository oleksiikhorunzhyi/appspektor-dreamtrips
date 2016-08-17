package com.worldventures.dreamtrips.modules.feed.api.response;

import android.os.Handler;

import com.techery.spares.utils.delegate.NotificationCountEventDelegate;

import java.util.List;

import retrofit.client.Header;

import static com.worldventures.dreamtrips.core.utils.InterceptingOkClient.ResponseHeaderListener;

public class HeaderChangedInformerListener implements ResponseHeaderListener {

   private static final long DELAY = 1000L;
   private NotificationCountEventDelegate notificationCountEventDelegate;
   //
   private Handler handler = new Handler();
   private Runnable runnable = () -> notificationCountEventDelegate.post(null);

   public HeaderChangedInformerListener(NotificationCountEventDelegate notificationCountEventDelegate) {
      this.notificationCountEventDelegate = notificationCountEventDelegate;
   }

   @Override
   public void onResponse(List<Header> headers) {
      handler.removeCallbacks(runnable);
      handler.postDelayed(runnable, DELAY);
   }


}
