package com.worldventures.dreamtrips.modules.gcm.service;

import android.app.IntentService;
import android.content.Intent;

import com.worldventures.core.janet.Injector;
import com.worldventures.dreamtrips.modules.common.command.SubscribeToPushNotificationsCommand;

import javax.inject.Inject;

public class RegistrationIntentService extends IntentService {

   public static final String TOKEN_CHANGED = "token_changed";

   @Inject SubscribeToPushNotificationsInteractor subscribeToPushNotificationsInteractor;

   public RegistrationIntentService() {
      super("RegistrationIntentService");
   }

   @Override
   public void onCreate() {
      super.onCreate();
      ((Injector) getApplication()).inject(this);
   }

   @Override
   protected void onHandleIntent(Intent intent) {
      boolean isTokenChangedFromCallback = intent.getBooleanExtra(TOKEN_CHANGED, false);
      subscribeToPushNotificationsInteractor.subscribeToPushNotificationsActionPipe()
            .send(new SubscribeToPushNotificationsCommand(isTokenChangedFromCallback));
   }
}
