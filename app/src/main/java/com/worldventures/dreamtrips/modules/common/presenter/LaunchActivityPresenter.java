package com.worldventures.dreamtrips.modules.common.presenter;

import com.messenger.synchmechanism.MessengerConnector;
import com.techery.spares.utils.ValidationUtils;
import com.worldventures.dreamtrips.core.repository.SnappyRepository;
import com.worldventures.dreamtrips.core.utils.tracksystem.AnalyticsInteractor;
import com.worldventures.dreamtrips.core.utils.tracksystem.TrackingHelper;
import com.worldventures.dreamtrips.modules.auth.api.command.LoginCommand;
import com.worldventures.dreamtrips.modules.auth.service.LoginInteractor;
import com.worldventures.dreamtrips.modules.auth.service.analytics.LoginAction;
import com.worldventures.dreamtrips.modules.auth.service.analytics.LoginErrorAction;
import com.worldventures.dreamtrips.modules.auth.util.SessionUtil;
import com.worldventures.dreamtrips.modules.background_uploading.service.BackgroundUploadingInteractor;
import com.worldventures.dreamtrips.modules.background_uploading.service.RestoreCompoundOperationsCommand;
import com.worldventures.dreamtrips.modules.common.service.CleanTempDirectoryCommand;
import com.worldventures.dreamtrips.modules.common.service.ClearStoragesInteractor;
import com.worldventures.dreamtrips.modules.common.view.ApiErrorView;
import com.worldventures.dreamtrips.modules.dtl.model.location.DtlLocation;
import com.worldventures.dreamtrips.modules.dtl.service.DtlLocationInteractor;
import com.worldventures.dreamtrips.modules.dtl.service.action.DtlLocationCommand;

import javax.inject.Inject;

import icepick.State;
import io.techery.janet.helper.ActionStateSubscriber;
import rx.android.schedulers.AndroidSchedulers;

import static com.worldventures.dreamtrips.util.ValidationUtils.isPasswordValid;
import static com.worldventures.dreamtrips.util.ValidationUtils.isUsernameValid;

public class LaunchActivityPresenter extends ActivityPresenter<LaunchActivityPresenter.View> {

   @Inject ClearStoragesInteractor clearStoragesInteractor;
   @Inject SnappyRepository db;
   @Inject AnalyticsInteractor analyticsInteractor;
   @Inject DtlLocationInteractor dtlLocationInteractor;
   @Inject LoginInteractor loginInteractor;
   @Inject MessengerConnector messengerConnector;
   @Inject BackgroundUploadingInteractor backgroundUploadingInteractor;

   @State boolean dtlInitDone;

   @Override
   public void takeView(View view) {
      super.takeView(view);
      launchModeBasedOnExistingSession();

      if (!dtlInitDone) {
         initDtl();
         dtlInitDone = true;
      }

      loginInteractor.loginActionPipe()
            .observeWithReplay()
            .observeOn(AndroidSchedulers.mainThread())
            .compose(bindView())
            .subscribe(new ActionStateSubscriber<LoginCommand>()
                  .onStart(loginCommand -> view.showLoginProgress())
                  .onSuccess(loginCommand -> {
                     loginInteractor.loginActionPipe().clearReplays();
                     launchModeBasedOnExistingSession();
                  })
                  .onFail((loginCommand, throwable) -> {
                     handleError(loginCommand, throwable);
                     view.dismissLoginProgress();
                     loginInteractor.loginActionPipe().clearReplays();
                     analyticsInteractor.analyticsActionPipe().send(new LoginErrorAction());
                  }));
   }

   private void launchModeBasedOnExistingSession() {
      if (SessionUtil.isUserSessionTokenExist(appSessionHolder)) {
         splashMode();
      } else {
         loginMode();
      }
   }

   private void splashMode() {
      view.openSplash();
      clearStoragesInteractor.cleanTempDirectoryPipe()
            .createObservable(new CleanTempDirectoryCommand())
            .compose(bindViewToMainComposer())
            .subscribe(new ActionStateSubscriber<CleanTempDirectoryCommand>()
                  .onSuccess(cleanTempDirectoryCommand -> onAuthSuccess())
                  .onFail((cleanTempDirectoryCommand, throwable) -> onAuthSuccess()));
   }

   private void loginMode() {
      view.openLogin();
   }

   @Override
   protected boolean canShowTermsDialog() {
      return false;
   }

   public void initDtl() {
      db.cleanLastSelectedOffersOnlyToggle();
      db.cleanLastMapCameraPosition();
      dtlLocationInteractor.locationPipe().send(DtlLocationCommand.change(DtlLocation.UNDEFINED));
   }

   public void loginAction() {
      String username = view.getUsername();
      String userPassword = view.getUserPassword();

      ValidationUtils.VResult usernameValid = isUsernameValid(username);
      ValidationUtils.VResult passwordValid = isPasswordValid(userPassword);

      if (!usernameValid.isValid() || !passwordValid.isValid()) {
         view.showLocalErrors(usernameValid.getMessage(), passwordValid.getMessage());
         return;
      }

      loginInteractor.loginActionPipe().send(new LoginCommand(username, userPassword));
   }

   private void onAuthSuccess() {
      backgroundUploadingInteractor.restoreCompoundOperationsPipe().send(new RestoreCompoundOperationsCommand());
      analyticsInteractor.analyticsActionPipe().send(new LoginAction(appSessionHolder.get()
            .get().getUser().getUsername()));
      TrackingHelper.setUserId(getAccount().getUsername(), Integer.toString(getAccount().getId()));
      messengerConnector.connect();
      view.openMain();
   }

   @Override
   protected boolean canShowOfflineAlert() {
      return false;
   }

   public interface View extends ActivityPresenter.View, ApiErrorView {

      void openLogin();

      void openSplash();

      void openMain();

      void dismissLoginProgress();

      void showLoginProgress();

      void showLocalErrors(int userNameError, int passwordError);

      String getUsername();

      String getUserPassword();
   }
}
