package com.worldventures.dreamtrips.social.ui.activity.presenter;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GooglePlayServicesUtil;
import com.worldventures.core.component.RootComponentsProvider;
import com.worldventures.dreamtrips.modules.common.presenter.ActivityPresenter;
import com.worldventures.dreamtrips.modules.gcm.service.RegistrationIntentService;
import com.worldventures.dreamtrips.qa.QaConfig;
import com.worldventures.dreamtrips.social.ui.background_uploading.service.PingAssetStatusInteractor;
import com.worldventures.dreamtrips.social.ui.background_uploading.service.command.LaunchUpdatingVideoProcessingCommand;

import javax.inject.Inject;

public class MainActivityPresenter extends ActivityPresenter<MainActivityPresenter.View> {

   @Inject RootComponentsProvider rootComponentsProvider;
   @Inject PingAssetStatusInteractor pingAssetStatusInteractor;
   @Inject QaConfig qaConfig;

   @Override
   public void takeView(View view) {
      super.takeView(view);
      checkGoogleServices();
      updateVideoAttachmenStatus();
   }

   private void checkGoogleServices() {
      int code = GooglePlayServicesUtil.isGooglePlayServicesAvailable(context);
      if (code != ConnectionResult.SUCCESS && qaConfig.getApp().getEnableBlockingInteractions()) {
         GooglePlayServicesUtil.getErrorDialog(code, activity, 0).show();
      } else {
         activityRouter.startService(RegistrationIntentService.class);
      }
   }

   private void updateVideoAttachmenStatus() {
      pingAssetStatusInteractor.launchUpdatingVideoProcessingPipe()
            .send(new LaunchUpdatingVideoProcessingCommand());
   }

   public interface View extends ActivityPresenter.View {

      void setTitle(int title);

      void makeActionBarGone(boolean hide);
   }
}
