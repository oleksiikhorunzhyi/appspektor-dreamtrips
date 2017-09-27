package com.worldventures.dreamtrips.modules.common.presenter;

import com.messenger.synchmechanism.MessengerConnector;
import com.techery.spares.utils.ValidationUtils;
import com.worldventures.dreamtrips.core.repository.SnappyRepository;
import com.worldventures.dreamtrips.core.utils.tracksystem.AnalyticsInteractor;
import com.worldventures.dreamtrips.core.utils.tracksystem.command.ClearHeadersCommand;
import com.worldventures.dreamtrips.core.utils.tracksystem.command.SetUserIdsHeadersCommand;
import com.worldventures.dreamtrips.modules.auth.api.command.LoginCommand;
import com.worldventures.dreamtrips.modules.auth.service.AuthInteractor;
import com.worldventures.dreamtrips.modules.auth.service.analytics.LoginAction;
import com.worldventures.dreamtrips.modules.auth.service.analytics.LoginErrorAction;
import com.worldventures.dreamtrips.modules.auth.util.SessionUtil;
import com.worldventures.dreamtrips.modules.common.command.CleanTempDirectoryCommand;
import com.worldventures.dreamtrips.modules.common.delegate.HttpResponseSnifferDelegate;
import com.worldventures.dreamtrips.modules.common.service.ClearStoragesInteractor;
import com.worldventures.dreamtrips.modules.common.service.InitializerInteractor;
import com.worldventures.dreamtrips.modules.dtl.service.DtlLocationInteractor;
import com.worldventures.dreamtrips.social.ui.background_uploading.service.BackgroundUploadingInteractor;
import com.worldventures.dreamtrips.social.ui.background_uploading.service.command.RestoreCompoundOperationsCommand;

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
   @Inject AuthInteractor authInteractor;
   @Inject MessengerConnector messengerConnector;
   @Inject BackgroundUploadingInteractor backgroundUploadingInteractor;
   // Lazy dagger won't instantiate unless injected. Do not delete unused below!
   @Inject DtlLocationInteractor dtlLocationInteractor;
   @Inject InitializerInteractor initializerInteractor;
   @Inject HttpResponseSnifferDelegate httpResponseSnifferDelegate;

   @State boolean dtlInitDone;
   @State boolean userAlreadyLoggedIn = true;

   @Override
   public void takeView(View view) {
      super.takeView(view);
      launchModeBasedOnExistingSession();

      if (!dtlInitDone) {
         initDtl();
         dtlInitDone = true;
      }

      authInteractor.loginActionPipe()
            .observeWithReplay()
            .observeOn(AndroidSchedulers.mainThread())
            .compose(bindView())
            .subscribe(new ActionStateSubscriber<LoginCommand>()
                  .onStart(loginCommand -> view.showLoginProgress())
                  .onSuccess(loginCommand -> {
                     userAlreadyLoggedIn = false;
                     authInteractor.loginActionPipe().clearReplays();
                     launchModeBasedOnExistingSession();
                  })
                  .onFail((loginCommand, throwable) -> {
                     handleError(loginCommand, throwable);
                     view.dismissLoginProgress();
                     authInteractor.loginActionPipe().clearReplays();
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

   private void initDtl() {
      db.cleanLastMapCameraPosition(); // TODO :: 26.09.16 move to PresetationInteractor
   }

   public void loginAction() {
      analyticsInteractor.clearAdobeHeadersPipe().send(new ClearHeadersCommand());
      String username = view.getUsername();
      String userPassword = view.getUserPassword();

      ValidationUtils.VResult usernameValid = isUsernameValid(username);
      ValidationUtils.VResult passwordValid = isPasswordValid(userPassword);

      if (!usernameValid.isValid() || !passwordValid.isValid()) {
         view.showLocalErrors(usernameValid.getMessage(), passwordValid.getMessage());
         return;
      }

      authInteractor.loginActionPipe().send(new LoginCommand(username, userPassword));
   }

   private void onAuthSuccess() {
      backgroundUploadingInteractor.restoreCompoundOperationsPipe().send(new RestoreCompoundOperationsCommand());
      analyticsInteractor.analyticsActionPipe().send(new LoginAction(appSessionHolder.get()
            .get().getUser().getUsername(), userAlreadyLoggedIn));
      analyticsInteractor.setUserIdsPipe().send(new SetUserIdsHeadersCommand(getAccount().getUsername(),
            Integer.toString(getAccount().getId())));
      messengerConnector.connect();
      view.openMain();
   }

   @Override
   protected boolean canShowOfflineAlert() {
      return false;
   }

   public interface View extends ActivityPresenter.View {

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
