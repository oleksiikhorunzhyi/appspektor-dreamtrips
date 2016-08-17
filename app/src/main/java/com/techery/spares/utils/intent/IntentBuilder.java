package com.techery.spares.utils.intent;

import android.content.Context;
import android.content.Intent;

public class IntentBuilder {
   private final Context context;

   public IntentBuilder(Context context) {
      this.context = context;
   }

   public Intent buildSimpleIntent(Class<?> targetClass) {
      return new Intent(context, targetClass);
   }
}
