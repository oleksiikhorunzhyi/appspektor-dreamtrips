package com.worldventures.dreamtrips.modules.common.presenter;

import android.app.Activity;
import android.content.res.Configuration;

import com.worldventures.core.model.User;
import com.worldventures.core.model.session.UserSession;
import com.worldventures.core.modules.auth.api.command.LogoutCommand;
import com.worldventures.core.modules.auth.api.command.UpdateUserCommand;
import com.worldventures.core.modules.auth.service.AuthInteractor;
import com.worldventures.core.modules.auth.service.ReLoginInteractor;
import com.worldventures.core.storage.complex_objects.Optional;
import com.worldventures.dreamtrips.core.rx.RxView;
import com.worldventures.dreamtrips.core.rx.composer.IoToMainComposer;
import com.worldventures.dreamtrips.core.utils.LocaleSwitcher;
import com.worldventures.dreamtrips.modules.config.delegate.VersionUpdateDelegate;
import com.worldventures.dreamtrips.modules.config.model.UpdateRequirement;
import com.worldventures.dreamtrips.modules.config.service.AppConfigurationInteractor;
import com.worldventures.dreamtrips.modules.config.service.command.ConfigurationCommand;

import javax.inject.Inject;

import icepick.State;
import io.techery.janet.ActionState;
import io.techery.janet.helper.ActionStateSubscriber;
import io.techery.janet.http.exception.HttpException;
import timber.log.Timber;

public class ActivityPresenter<VT extends ActivityPresenter.View> extends Presenter<VT> {

   @Inject protected Activity activity;
   @Inject protected LocaleSwitcher localeSwitcher;

   @Inject ReLoginInteractor reLoginInteractor;
   @Inject protected AuthInteractor authInteractor;
   @Inject VersionUpdateDelegate versionUpdateDelegate;
   @Inject AppConfigurationInteractor appConfigurationInteractor;

   @State boolean isTermsShown;

   @Override
   public void onInjected() {
      super.onInjected();
      setupUserLocale();
   }

   @Override
   public void takeView(VT view) {
      super.takeView(view);
      checkTermsAndConditionFromHolder();
      subscribeToUserUpdate();
      subscribeToLoginErrors();
   }

   @Override
   public void onResume() {
      super.onResume();
      //Some third-party libraries can change the locale.
      setupUserLocale();
      subscribeToAppVersionUpdates();
   }

   private void subscribeToLoginErrors() {
      reLoginInteractor.loginHttpActionPipe().observe()
            .compose(bindViewToMainComposer())
            .subscribe(state -> {
               if (state.status == ActionState.Status.FAIL) {
                  if (isLoginError(state.exception)) {
                     logout();
                  }
               }
            });
   }

   private void subscribeToAppVersionUpdates() {
      appConfigurationInteractor.configurationCommandActionPipe()
            .observeWithReplay()
            .compose(bindUntilPauseIoToMainComposer())
            .subscribe(new ActionStateSubscriber<ConfigurationCommand>()
                  .onProgress((command, integer) -> processUpdateRequirement(command.getResult().getUpdateRequirement()))
                  .onSuccess(command -> processUpdateRequirement(command.getResult().getUpdateRequirement()))
                  .onFail((versionCheckAction, throwable) ->
                        Timber.w(throwable, "Could not check latest app version")));
   }

   private void processUpdateRequirement(UpdateRequirement updateRequirement) {
      if (!activity.isFinishing()) {
         versionUpdateDelegate.processUpdateRequirement(updateRequirement);
      }
   }

   private boolean isLoginError(Throwable error) {
      if (error == null) return false;
      if (error instanceof HttpException) { // for janet-http
         HttpException cause = (HttpException) error;
         return cause.getResponse() != null && cause.getResponse().getStatus() == 422;
      } else if (error.getCause() != null) {
         return isLoginError(error.getCause());
      }
      return false;
   }

   public void logout() {
      authInteractor.logoutPipe().send(new LogoutCommand());
   }

   @Override
   public void dropView() {
      super.dropView();
      activity = null;
   }

   public void onConfigurationChanged(Configuration configuration) {
      localeSwitcher.onConfigurationLocaleChanged(configuration.locale);
   }

   private void checkTermsAndConditionFromHolder() {
      Optional<UserSession> userSession = appSessionHolder.get();
      if (userSession.isPresent()) {
         checkTermsAndConditions(userSession.get().user());
      }
   }

   protected boolean canShowTermsDialog() {
      return !activity.isFinishing() && !isTermsShown;
   }

   protected void setupUserLocale() {
      localeSwitcher.applyLocaleFromSession();
   }

   private void subscribeToUserUpdate() {
      view.bindUntilDropView(authInteractor.updateUserPipe().observe().compose(new IoToMainComposer<>()))
            .subscribe(new ActionStateSubscriber<UpdateUserCommand>().onSuccess(updateUserCommand -> checkTermsAndConditions(updateUserCommand
                  .getResult())));
   }

   private boolean checkTermsAndConditions(User user) {
      if (user == null || user.isTermsAccepted() || !canShowTermsDialog()) return true;
      isTermsShown = true;
      view.showTermsDialog();
      return false;
   }

   public interface View extends RxView {

      void showTermsDialog();
   }
}
