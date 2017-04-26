package com.techery.spares.ui.routing;

import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;

import com.techery.spares.ui.activity.InjectingActivity;
import com.worldventures.dreamtrips.modules.common.presenter.ComponentPresenter;

public class BaseRouter {
   public static final String EXTRA_BUNDLE = "EXTRA_BUNDLE";

   private final Context context;

   public BaseRouter(Context context) {
      this.context = context;
   }

   protected void startActivity(Class<? extends InjectingActivity> activityClass) {
      startActivity(activityClass, null, -1);
   }

   protected void startActivityAndClearTop(Class<? extends InjectingActivity> activityClass) {
      startActivity(activityClass, Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
   }

   protected void startActivity(Class<? extends InjectingActivity> activityClass, int flags) {
      startActivity(activityClass, null, flags);
   }

   protected void startActivity(Class<? extends InjectingActivity> activityClass, Bundle params, int flags) {
      Intent intent = new Intent(getContext(), activityClass);

      if (params != null) {
         intent.putExtras(params);
      }

      if (flags > 0) {
         intent.setFlags(flags);
      }

      startActivityIntent(intent);
   }

   protected void startActivity(Intent intent) {
      getContext().startActivity(intent);
   }

   @Deprecated
   protected void startActivity(Class<? extends InjectingActivity> activityClass, Bundle bundle) {
      Intent intent = new Intent(getContext(), activityClass);
      if (bundle != null) {
         intent.putExtra(EXTRA_BUNDLE, bundle);
      }
      getContext().startActivity(intent);
   }

   protected void startActivityWithArgs(Class<? extends InjectingActivity> activityClass, Bundle args) {
      startActivityWithArgs(activityClass, args, 0);
   }

   protected void startActivityWithArgs(Class<? extends InjectingActivity> activityClass, Bundle args, int flags) {
      Intent intent = new Intent(getContext(), activityClass);
      intent.putExtra(ComponentPresenter.COMPONENT_EXTRA, args);
      if (flags > 0) {
         intent.setFlags(flags);
      }
      getContext().startActivity(intent);
   }

   protected void startActivityIntent(Intent intent) {
      getContext().startActivity(intent);
   }

   protected void startService(Class<? extends Service> serviceClass) {
      startServiceIntent(new Intent(getContext(), serviceClass));
   }

   protected void startServiceIntent(Intent intent) {
      getContext().startService(intent);
   }

   public Context getContext() {
      return this.context;
   }

   protected void openUri(Uri uri) {
      Intent videoClient = new Intent(Intent.ACTION_VIEW);
      videoClient.setData(uri);
      startActivityIntent(videoClient);
   }
}
