package com.worldventures.dreamtrips.modules.common.presenter;

import android.support.annotation.Nullable;
import android.text.TextUtils;

import com.messenger.synchmechanism.MessengerConnector;
import com.techery.spares.utils.ValidationUtils;
import com.worldventures.dreamtrips.core.navigation.ActivityRouter;
import com.worldventures.dreamtrips.core.repository.SnappyRepository;
import com.worldventures.dreamtrips.core.utils.tracksystem.TrackingHelper;
import com.worldventures.dreamtrips.modules.auth.api.command.LoginCommand;
import com.worldventures.dreamtrips.modules.auth.api.command.UpdateAuthInfoCommand;
import com.worldventures.dreamtrips.modules.auth.service.AuthInteractor;
import com.worldventures.dreamtrips.modules.auth.service.LoginInteractor;
import com.worldventures.dreamtrips.modules.common.model.User;
import com.worldventures.dreamtrips.modules.common.presenter.delegate.ClearDirectoryDelegate;
import com.worldventures.dreamtrips.modules.common.view.ApiErrorView;
import com.worldventures.dreamtrips.modules.common.view.util.DrawableUtil;
import com.worldventures.dreamtrips.modules.dtl.model.location.DtlLocation;
import com.worldventures.dreamtrips.modules.dtl.service.DtlLocationInteractor;
import com.worldventures.dreamtrips.modules.dtl.service.action.DtlLocationCommand;

import javax.inject.Inject;

import io.techery.janet.helper.ActionStateSubscriber;
import rx.android.schedulers.AndroidSchedulers;

import static com.worldventures.dreamtrips.util.ValidationUtils.isPasswordValid;
import static com.worldventures.dreamtrips.util.ValidationUtils.isUsernameValid;

public class LaunchActivityPresenter extends ActivityPresenter<LaunchActivityPresenter.View> {

   @Inject SnappyRepository snappyRepository;
   @Inject ClearDirectoryDelegate clearTemporaryDirectoryDelegate;
   @Inject DrawableUtil drawableUtil;
   @Inject SnappyRepository db;
   @Inject DtlLocationInteractor dtlLocationInteractor;
   @Inject LoginInteractor loginInteractor;
   @Inject MessengerConnector messengerConnector;
   @Inject AuthInteractor authInteractor;

   public void detectMode(@Nullable String type) {
      if (TextUtils.isEmpty(type)) {
         splashMode();
         return;
      }
      switch (type) {
         case ActivityRouter.LAUNCH_LOGIN:
            loginMode();
            break;
         case ActivityRouter.LAUNCH_SPLASH:
         default:
            splashMode();
            break;
      }
   }

   private void splashMode() {
      view.openSplash();
      clearTemporaryDirectoryDelegate.clearTemporaryDirectory();
      drawableUtil.removeCacheImages();
      startPreloadChain();
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

      loginInteractor.loginActionPipe()
            .createObservable(new LoginCommand(username, userPassword))
            .observeOn(AndroidSchedulers.mainThread())
            .compose(bindView())
            .subscribe(new ActionStateSubscriber<LoginCommand>().onStart(loginCommand -> view.showLoginProgress())
                  .onSuccess(loginCommand -> {
                     User user = loginCommand.getResult().getUser();
                     TrackingHelper.login(user.getEmail());
                     TrackingHelper.setUserId(Integer.toString(user.getId()));
                     splashMode();
                  })
                  .onFail((loginCommand, throwable) -> {
                     TrackingHelper.loginError();
                     view.alertLogin(loginCommand.getErrorMessage());
                  }));

   }

   public void startPreloadChain() {
      authInteractor.updateAuthInfoCommandActionPipe()
            .createObservable(new UpdateAuthInfoCommand())
            .observeOn(AndroidSchedulers.mainThread())
            .compose(bindView())
            .subscribe(new ActionStateSubscriber<UpdateAuthInfoCommand>().onSuccess(updateAuthInfoCommand -> onAuthSuccess())
                  .onFail((updateAuthInfoCommand, throwable) -> loginMode()));
   }

   private void onAuthSuccess() {
      TrackingHelper.setUserId(Integer.toString(appSessionHolder.get().get().getUser().getId()));
      messengerConnector.connect();
      view.openMain();
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
