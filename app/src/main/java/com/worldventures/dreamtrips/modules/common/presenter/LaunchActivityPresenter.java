package com.worldventures.dreamtrips.modules.common.presenter;

import com.messenger.synchmechanism.MessengerConnector;
import com.worldventures.core.model.AppVersionHolder;
import com.worldventures.core.modules.auth.api.command.LoginCommand;
import com.worldventures.core.modules.auth.service.AuthInteractor;
import com.worldventures.core.modules.auth.service.analytics.LoginAction;
import com.worldventures.core.modules.auth.service.analytics.LoginErrorAction;
import com.worldventures.core.modules.auth.util.SessionUtil;
import com.worldventures.core.service.analytics.command.ClearHeadersCommand;
import com.worldventures.core.service.analytics.command.SetUserIdsHeadersCommand;
import com.worldventures.core.utils.ValidationUtils;
import com.worldventures.dreamtrips.BuildConfig;
import com.worldventures.dreamtrips.core.repository.SnappyRepository;
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

   @Inject AuthInteractor authInteractor;
   @Inject MessengerConnector messengerConnector;
   @Inject BackgroundUploadingInteractor backgroundUploadingInteractor;
   // Lazy dagger won't instantiate unless injected. Do not delete unused below!
   @Inject DtlLocationInteractor dtlLocationInteractor;
   @Inject InitializerInteractor initializerInteractor;
   @Inject HttpResponseSnifferDelegate httpResponseSnifferDelegate;
   @Inject AppVersionHolder appVersionHolder;

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
                     appVersionHolder.put(BuildConfig.VERSION_CODE);
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
      if (isNewVersion()) {
         invalidateVersion();
      } else {
         clearStorage();
      }
   }

   private boolean isNewVersion() {
      return !appVersionHolder.get().isPresent() || BuildConfig.VERSION_CODE > appVersionHolder.get().get();
   }

   private void invalidateVersion() {
      authInteractor.loginActionPipe().send(new LoginCommand());
   }

   private void clearStorage() {
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
            .get().user().getUsername(), userAlreadyLoggedIn));
      analyticsInteractor.setUserIdsPipe().send(new SetUserIdsHeadersCommand(getAccount().getUsername(),
            Integer.toString(getAccount().getId())));
      messengerConnector.connect();
      view.openMainOrWallet();
   }

   @Override
   protected boolean canShowOfflineAlert() {
      return false;
   }

   public interface View extends ActivityPresenter.View {

      void openLogin();

      void openSplash();

      void openMainOrWallet();

      void dismissLoginProgress();

      void showLoginProgress();

      void showLocalErrors(int userNameError, int passwordError);

      String getUsername();

      String getUserPassword();
   }
}
