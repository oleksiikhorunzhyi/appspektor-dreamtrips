package com.techery.spares.utils;

import android.app.Activity;
import android.app.Application;
import android.os.Bundle;

public class SimpleActivityLifecycleCallbacks implements Application.ActivityLifecycleCallbacks {

   @Override
   public void onActivityCreated(Activity activity, Bundle savedInstanceState) {
      //do nothing
   }

   @Override
   public void onActivityStarted(Activity activity) {
      //do nothing
   }

   @Override
   public void onActivityResumed(Activity activity) {
      //do nothing
   }

   @Override
   public void onActivityPaused(Activity activity) {
      //do nothing
   }

   @Override
   public void onActivityStopped(Activity activity) {
      //do nothing
   }

   @Override
   public void onActivitySaveInstanceState(Activity activity, Bundle outState) {
      //do nothing
   }

   @Override
   public void onActivityDestroyed(Activity activity) {
      //do nothing
   }
}
