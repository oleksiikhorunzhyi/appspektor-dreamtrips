package com.techery.spares.utils.intent;

import android.content.Context;

public class ServiceLauncher {
   private final Context context;
   private final IntentBuilder intentBuilder;

   public ServiceLauncher(Context context, IntentBuilder intentBuilder) {
      this.context = context;
      this.intentBuilder = intentBuilder;
   }

   public void launch(Class<?> serviceClass) {
      this.context.startService(intentBuilder.buildSimpleIntent(serviceClass));
   }

   public void stop(Class<?> serviceClass) {
      this.context.stopService(intentBuilder.buildSimpleIntent(serviceClass));
   }
}
