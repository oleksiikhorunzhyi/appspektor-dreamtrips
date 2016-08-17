package com.worldventures.dreamtrips.modules.gcm;

import android.content.Context;

import com.google.gson.Gson;
import com.techery.spares.module.qualifier.ForApplication;
import com.techery.spares.utils.delegate.NotificationCountEventDelegate;
import com.worldventures.dreamtrips.core.repository.SnappyRepository;
import com.worldventures.dreamtrips.modules.gcm.delegate.NotificationDataParser;
import com.worldventures.dreamtrips.modules.gcm.delegate.NotificationDelegate;
import com.worldventures.dreamtrips.modules.gcm.delegate.NotificationFactoryHolder;
import com.worldventures.dreamtrips.modules.gcm.service.PushListenerService;
import com.worldventures.dreamtrips.modules.gcm.service.RegistrationIntentService;

import dagger.Module;
import dagger.Provides;

@Module(
      injects = {RegistrationIntentService.class, PushListenerService.class},
      includes = {NotificationFactoryModule.class},
      complete = false,
      library = true)
public class GcmModule {

   @Provides
   NotificationDelegate provideNotificationDelegate(@ForApplication Context context, NotificationCountEventDelegate notificationCountEventDelegate, SnappyRepository repository, NotificationFactoryHolder notificationFactoryHolder) {
      return new NotificationDelegate(context, notificationCountEventDelegate, repository, notificationFactoryHolder);
   }

   @Provides
   NotificationDataParser provideDataParser(Gson gson) {
      return new NotificationDataParser(gson);
   }

}
