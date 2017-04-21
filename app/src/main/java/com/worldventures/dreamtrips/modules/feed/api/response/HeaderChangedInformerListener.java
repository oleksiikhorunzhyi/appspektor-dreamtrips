package com.worldventures.dreamtrips.modules.feed.api.response;

import com.techery.spares.utils.delegate.NotificationCountEventDelegate;
import com.worldventures.dreamtrips.api.api_common.BaseHttpAction;
import com.worldventures.dreamtrips.core.janet.api_lib.NewDreamTripsHttpService;

import java.util.concurrent.TimeUnit;

import rx.Observable;


public class HeaderChangedInformerListener implements NewDreamTripsHttpService.ResponseListener {

   private static final long DELAY = 1000L;
   private NotificationCountEventDelegate notificationCountEventDelegate;

   public HeaderChangedInformerListener(NotificationCountEventDelegate notificationCountEventDelegate) {
      this.notificationCountEventDelegate = notificationCountEventDelegate;
   }

   @Override
   public void onResponse(BaseHttpAction baseHttpAction) {
      Observable.timer(DELAY, TimeUnit.MILLISECONDS)
            .subscribe(delay -> notificationCountEventDelegate.post(null));
   }

}
