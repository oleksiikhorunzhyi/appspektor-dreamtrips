package com.worldventures.dreamtrips.modules.common.presenter;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GooglePlayServicesUtil;
import com.worldventures.dreamtrips.BuildConfig;
import com.worldventures.dreamtrips.core.component.RootComponentsProvider;
import com.worldventures.dreamtrips.modules.gcm.service.RegistrationIntentService;

import javax.inject.Inject;

public class MainActivityPresenter extends ActivityPresenter<MainActivityPresenter.View> {

   @Inject RootComponentsProvider rootComponentsProvider;

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

   public interface View extends ActivityPresenter.View {

      void setTitle(int title);

      void makeActionBarGone(boolean hide);
   }
}
