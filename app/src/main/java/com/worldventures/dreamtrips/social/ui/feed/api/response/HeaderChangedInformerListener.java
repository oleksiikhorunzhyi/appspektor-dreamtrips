package com.worldventures.dreamtrips.social.ui.feed.api.response;

import com.techery.spares.utils.delegate.NotificationCountEventDelegate;
import com.worldventures.dreamtrips.api.api_common.BaseHttpAction;
import com.worldventures.dreamtrips.core.janet.api_lib.ResponseListener;

import java.util.concurrent.TimeUnit;

import rx.Observable;

public class HeaderChangedInformerListener implements ResponseListener {

   private static final long DELAY = 1000L;

   private final NotificationCountEventDelegate notificationCountEventDelegate;

   public HeaderChangedInformerListener(NotificationCountEventDelegate notificationCountEventDelegate) {
      this.notificationCountEventDelegate = notificationCountEventDelegate;
   }

   @Override
   public void onResponse(BaseHttpAction baseHttpAction) {
      Observable.timer(DELAY, TimeUnit.MILLISECONDS)
            .subscribe(delay -> notificationCountEventDelegate.post(null));
   }
}
