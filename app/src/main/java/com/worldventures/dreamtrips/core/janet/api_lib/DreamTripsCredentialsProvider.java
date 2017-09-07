package com.worldventures.dreamtrips.core.janet.api_lib;

import com.techery.spares.session.SessionHolder;
import com.worldventures.dreamtrips.api.session.model.Device;
import com.worldventures.dreamtrips.core.session.UserSession;

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
      return new CredentialsStorage(userSession.getUsername(),
            userSession.getUserPassword(), deviceSource.toBlocking().first());
   }
}
