package com.techery.spares.ui.routing;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;

import com.techery.spares.ui.activity.InjectingActivity;

public class ActivityBoundRouter extends BaseRouter {

   private final Activity activity;

   public ActivityBoundRouter(Activity activity) {
      super(activity);
      this.activity = activity;
   }

   protected Activity getActivity() {
      return activity;
   }

   public void finish() {
      activity.finish();
   }

   protected void startNewAndFinishCurrentActivity(Class<? extends InjectingActivity> activityClass) {
      startActivity(activityClass);
      finish();
   }

   protected void startForResult(Fragment from, Class<? extends InjectingActivity> activityClass, int requestCode) {
      startForResult(from, activityClass, requestCode, null);
   }

   protected void startForResult(Fragment from, Class<? extends InjectingActivity> activityClass, int requestCode, Bundle bundle) {
      if (from.isAdded()) {
         Intent intent = new Intent(getContext(), activityClass);

         if (bundle != null) {
            intent.putExtra(EXTRA_BUNDLE, bundle);
         }
         from.startActivityForResult(intent, requestCode);
      }
   }
}
