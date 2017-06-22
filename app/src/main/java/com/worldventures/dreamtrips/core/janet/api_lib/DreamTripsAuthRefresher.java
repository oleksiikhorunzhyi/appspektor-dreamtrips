package com.worldventures.dreamtrips.core.janet.api_lib;

import com.worldventures.dreamtrips.api.session.LoginHttpAction;
import com.worldventures.dreamtrips.mobilesdk.AuthRefresher;
import com.worldventures.dreamtrips.modules.auth.service.ReLoginInteractor;

import io.techery.janet.ActionState;
import io.techery.mappery.MapperyContext;
import rx.Observable;
import timber.log.Timber;

public class DreamTripsAuthRefresher implements AuthRefresher {

   private final ReLoginInteractor reLoginInteractor;
   private final CredentialsProvider credentialsProvider;
   private final AuthStorage authStorage;
   private final MapperyContext mapperyContext;

   public DreamTripsAuthRefresher(ReLoginInteractor reLoginInteractor, CredentialsProvider credentialsProvider,
         AuthStorage authStorage, MapperyContext mapperyContext) {
      this.reLoginInteractor = reLoginInteractor;
      this.credentialsProvider = credentialsProvider;
      this.authStorage = authStorage;
      this.mapperyContext = mapperyContext;
   }

   @Override
   public Observable<Boolean> refresh() {
      CredentialsStorage credentialsStorage = credentialsProvider.provideCredentials();
      LoginHttpAction loginAction = new LoginHttpAction(credentialsStorage.userName, credentialsStorage.password,
            credentialsStorage.device);
      ActionState<LoginHttpAction> loginState = reLoginInteractor.loginHttpActionPipe()
            .createObservable(loginAction).toBlocking().last();
      boolean reAuthSuccess = loginState.status == ActionState.Status.SUCCESS;
      if (reAuthSuccess) {
         authStorage.storeAuth(mapperyContext.convert(loginState.action.response(), authStorage.getAuthType()));
      } else {
         Timber.w(loginState.exception, "Login error");
      }
      return Observable.just(reAuthSuccess);
   }
}
