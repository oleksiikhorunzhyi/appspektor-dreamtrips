package com.techery.spares.ui.routing;

import android.app.Activity;
import android.content.Intent;

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

   public void startActivityResult(Intent intent, int requestCode) {
      activity.startActivityForResult(intent, requestCode);
   }
}
