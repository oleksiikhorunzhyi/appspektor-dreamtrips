package com.worldventures.dreamtrips.modules.common;

import com.techery.spares.utils.delegate.NotificationCountEventDelegate;
import com.worldventures.dreamtrips.core.janet.api_lib.NewDreamTripsHttpService;
import com.worldventures.dreamtrips.core.module.ApiModule;
import com.worldventures.dreamtrips.core.repository.SnappyRepository;
import com.worldventures.dreamtrips.modules.feed.api.response.HeaderChangedInformerListener;
import com.worldventures.dreamtrips.modules.feed.api.response.RequestCountResponseListener;

import dagger.Module;
import dagger.Provides;

@Module(
      injects = {ApiModule.class},
      complete = false,
      library = true)
public class ResponseSnifferModule {

   @Provides(type = Provides.Type.SET)
   NewDreamTripsHttpService.ResponseListener provideNotificationCountListener(SnappyRepository db) {
      return new RequestCountResponseListener(db);
   }

   @Provides(type = Provides.Type.SET)
   NewDreamTripsHttpService.ResponseListener provideHeaderChangedInformerResponseListener(NotificationCountEventDelegate notificationCountEventDelegate) {
      return new HeaderChangedInformerListener(notificationCountEventDelegate);
   }
}
