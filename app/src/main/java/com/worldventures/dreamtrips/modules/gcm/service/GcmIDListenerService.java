package com.worldventures.dreamtrips.modules.gcm.service;

import android.content.Intent;

import com.google.android.gms.iid.InstanceIDListenerService;

public class GcmIDListenerService extends InstanceIDListenerService {

   @Override
   public void onTokenRefresh() {
      // Fetch updated Instance ID token and notify our app's server of any changes (if applicable).
      Intent intent = new Intent(this, RegistrationIntentService.class);
      intent.putExtra(RegistrationIntentService.TOKEN_CHANGED, true);
      startService(intent);
   }
}
