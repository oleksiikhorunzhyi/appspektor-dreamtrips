package com.worldventures.dreamtrips.modules.common.presenter;

import com.messenger.synchmechanism.MessengerConnector;
import com.techery.spares.utils.ValidationUtils;
import com.worldventures.dreamtrips.core.repository.SnappyRepository;
import com.worldventures.dreamtrips.core.utils.tracksystem.TrackingHelper;
import com.worldventures.dreamtrips.modules.auth.api.command.LoginCommand;
import com.worldventures.dreamtrips.modules.auth.service.LoginInteractor;
import com.worldventures.dreamtrips.modules.auth.util.SessionUtil;
import com.worldventures.dreamtrips.modules.common.model.User;
import com.worldventures.dreamtrips.modules.common.presenter.delegate.ClearDirectoryDelegate;
import com.worldventures.dreamtrips.modules.common.view.ApiErrorView;
import com.worldventures.dreamtrips.modules.common.view.util.DrawableUtil;
import com.worldventures.dreamtrips.modules.dtl.service.DtlLocationInteractor;

import javax.inject.Inject;

import icepick.State;
import io.techery.janet.helper.ActionStateSubscriber;
import rx.android.schedulers.AndroidSchedulers;

import static com.worldventures.dreamtrips.util.ValidationUtils.isPasswordValid;
import static com.worldventures.dreamtrips.util.ValidationUtils.isUsernameValid;

public class LaunchActivityPresenter extends ActivityPresenter<LaunchActivityPresenter.View> {

   @Inject ClearDirectoryDelegate clearTemporaryDirectoryDelegate;
   @Inject DrawableUtil drawableUtil;
   @Inject SnappyRepository db;
   @Inject DtlLocationInteractor dtlLocationInteractor;
   @Inject LoginInteractor loginInteractor;
   @Inject MessengerConnector messengerConnector;

   @State boolean dtlInitDone;
   @State boolean clearCacheDone;

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
                     User user = loginCommand.getResult().getUser();
                     TrackingHelper.login(user.getEmail());
                     launchModeBasedOnExistingSession();
                  })
                  .onFail((loginCommand, throwable) -> {
                     loginInteractor.loginActionPipe().clearReplays();
                     TrackingHelper.loginError();
                     view.alertLogin(loginCommand.getErrorMessage());
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

      if (!clearCacheDone) {
         clearTemporaryDirectoryDelegate.clearTemporaryDirectory();
         drawableUtil.removeCacheImages();
         clearCacheDone = true;
      }

      onAuthSuccess();
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

      void alertLogin(String message);

      void showLoginProgress();

      void showLocalErrors(int userNameError, int passwordError);

      String getUsername();

      String getUserPassword();
   }
}
