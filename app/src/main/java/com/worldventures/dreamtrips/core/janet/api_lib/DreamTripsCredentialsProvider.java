package com.worldventures.dreamtrips.core.janet.api_lib;

import com.worldventures.core.model.session.SessionHolder;
import com.worldventures.core.model.session.UserSession;
import com.worldventures.dreamtrips.api.session.model.Device;

import rx.Observable;

public class DreamTripsCredentialsProvider implements CredentialsProvider {

   private final SessionHolder sessionHolder;
   private final Observable<Device> deviceSource;

   public DreamTripsCredentialsProvider(SessionHolder sessionHolder, Observable<Device> deviceSource) {
      this.sessionHolder = sessionHolder;
      this.deviceSource = deviceSource;
   }

   @Override
   public CredentialsStorage provideCredentials() {
      UserSession userSession = sessionHolder.get().get();
      return new CredentialsStorage(userSession.username(),
            userSession.userPassword(), deviceSource.toBlocking().first());
   }
}
