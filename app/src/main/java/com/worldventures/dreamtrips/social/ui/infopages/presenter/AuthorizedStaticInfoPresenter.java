package com.worldventures.dreamtrips.social.ui.infopages.presenter;

import com.worldventures.core.model.session.UserSession;
import com.worldventures.core.modules.auth.api.command.LoginCommand;
import com.worldventures.core.modules.auth.api.command.LogoutCommand;
import com.worldventures.core.modules.auth.service.AuthInteractor;

import java.util.concurrent.TimeUnit;

import javax.inject.Inject;

import io.techery.janet.helper.ActionStateSubscriber;
import rx.functions.Action0;
import timber.log.Timber;

public class AuthorizedStaticInfoPresenter<T extends AuthorizedStaticInfoPresenter.View> extends WebViewFragmentPresenter<T> {

   private static final int LIFE_DURATION = 30; // mins

   @Inject AuthInteractor authInteractor;

   public AuthorizedStaticInfoPresenter(String url) {
      super(url);
   }

   @Override
   public void load() {
      doWithAuth(super::load);
   }

   @Override
   protected void reload() {
      doWithAuth(super::reload);
   }

   private void doWithAuth(Action0 action) {
      UserSession userSession = appSessionHolder.get().get();
      if (userSession.getLastUpdate() > System.currentTimeMillis() - TimeUnit.MINUTES.toMillis(LIFE_DURATION)) {
         action.call();
      } else {
         view.setRefreshing(true);
         reLogin();
      }
   }

   public void reLogin() {
      authInteractor.loginActionPipe()
            .createObservable(new LoginCommand())
            .compose(bindUntilPauseIoToMainComposer())
            .subscribe(new ActionStateSubscriber<LoginCommand>().onSuccess(loginCommand -> onLoginSuccess())
                  .onFail((loginCommand, throwable) -> onLoginFail(throwable)));
   }

   protected void onLoginSuccess() {
      view.setRefreshing(false);
      reload();
   }

   private void onLoginFail(Throwable throwable) {
      Timber.e(throwable, "Can't login during WebView loading");
      authInteractor.logoutPipe().send(new LogoutCommand());
      view.setRefreshing(false);
   }

   public interface View extends WebViewFragmentPresenter.View {}
}
