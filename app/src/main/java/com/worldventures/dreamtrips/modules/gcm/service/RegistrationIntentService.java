package com.worldventures.dreamtrips.modules.gcm.service;

import android.content.Intent;

import com.techery.spares.service.InjectingIntentService;
import com.worldventures.dreamtrips.modules.common.api.janet.command.SubscribeToPushNotificationsCommand;
import com.worldventures.dreamtrips.modules.common.service.SubscribeToPushNotificationsInteractor;

import javax.inject.Inject;

public class RegistrationIntentService extends InjectingIntentService {

   public static final String TOKEN_CHANGED = "token_changed";

   @Inject SubscribeToPushNotificationsInteractor subscribeToPushNotificationsInteractor;

   public RegistrationIntentService() {
      super("RegistrationIntentService");
   }

   @Override
   protected void onHandleIntent(Intent intent) {
      boolean isTokenChangedFromCallback = intent.getBooleanExtra(TOKEN_CHANGED, false);
      subscribeToPushNotificationsInteractor.subscribeToPushNotificationsActionPipe()
            .send(new SubscribeToPushNotificationsCommand(isTokenChangedFromCallback));
   }
}
