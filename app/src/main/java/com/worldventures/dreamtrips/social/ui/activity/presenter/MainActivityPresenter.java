package com.worldventures.dreamtrips.social.ui.activity.presenter;

import com.worldventures.core.component.RootComponentsProvider;
import com.worldventures.core.utils.GoogleApiCheckUtilKt;
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
      GoogleApiCheckUtilKt.checkAvailability(
            context, () -> activityRouter.startService(RegistrationIntentService.class),
            errorCode -> {
               if (qaConfig.getApp().getEnableBlockingInteractions()) {
                  GoogleApiCheckUtilKt.showErrorDialog(activity, errorCode, 0);
               }
            });
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
