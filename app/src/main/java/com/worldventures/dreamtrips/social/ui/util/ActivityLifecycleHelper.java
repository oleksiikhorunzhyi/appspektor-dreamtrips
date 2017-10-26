package com.worldventures.dreamtrips.social.ui.util;

import android.app.Activity;
import android.app.Application;

import com.techery.spares.utils.SimpleActivityLifecycleCallbacks;
import com.worldventures.dreamtrips.social.ui.activity.ShowableComponent;

import rx.functions.Action0;

public class ActivityLifecycleHelper {

   public static void runTaskAfterShown(Application application, ShowableComponent component, Action0 task) {
      if (component.isVisibleOnScreen()) {
         task.call();
         return;
      }
      application.registerActivityLifecycleCallbacks(new SimpleActivityLifecycleCallbacks() {
         @Override
         public void onActivityResumed(Activity activity) {
            super.onActivityResumed(activity);
            task.call();
            application.unregisterActivityLifecycleCallbacks(this);
         }
      });
   }
}
