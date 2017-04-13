package com.worldventures.dreamtrips.modules.common.presenter;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GooglePlayServicesUtil;
import com.worldventures.dreamtrips.BuildConfig;
import com.worldventures.dreamtrips.modules.gcm.service.RegistrationIntentService;

public class MainActivityPresenter extends ActivityPresenter<MainActivityPresenter.View> {

   @Override
   public void takeView(View view) {
      super.takeView(view);
      checkGoogleServices();
   }

   private void checkGoogleServices() {
      int code = GooglePlayServicesUtil.isGooglePlayServicesAvailable(context);
      if (code != ConnectionResult.SUCCESS) {
         if (!BuildConfig.QA_AUTOMATION_MODE_ENABLED) GooglePlayServicesUtil.getErrorDialog(code, activity, 0).show();
      } else {
         activityRouter.startService(RegistrationIntentService.class);
      }
   }

   public interface View extends ActivityPresenter.View {}
}
