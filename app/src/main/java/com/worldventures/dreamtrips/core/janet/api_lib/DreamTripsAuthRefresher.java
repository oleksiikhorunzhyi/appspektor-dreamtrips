package com.worldventures.dreamtrips.core.janet.api_lib;

import com.worldventures.core.modules.auth.service.ReLoginInteractor;
import com.worldventures.dreamtrips.api.session.LoginHttpAction;
import com.worldventures.dreamtrips.core.janet.ResultStateOnlyComposer;
import com.worldventures.dreamtrips.mobilesdk.AuthRefresher;

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
      return reLoginInteractor.loginHttpActionPipe()
            .createObservable(new LoginHttpAction(credentialsStorage.userName, credentialsStorage.password, credentialsStorage.device))
            .compose(new ResultStateOnlyComposer<>())
            .doOnNext(result -> {
               if (result.status == ActionState.Status.SUCCESS) {
                  authStorage.storeAuth(mapperyContext.convert(result.action.response(), authStorage.getAuthType()));
               } else {
                  Timber.w(result.exception, "Can't refresh auth data");
               }
            })
            .map(result -> result.status == ActionState.Status.SUCCESS);
   }
}
