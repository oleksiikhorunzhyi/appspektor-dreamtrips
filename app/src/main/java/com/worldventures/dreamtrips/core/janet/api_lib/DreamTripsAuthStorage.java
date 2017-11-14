package com.worldventures.dreamtrips.core.janet.api_lib;

import com.worldventures.core.model.Session;
import com.worldventures.core.model.session.ImmutableUserSession;
import com.worldventures.core.model.session.SessionHolder;
import com.worldventures.core.service.AuthStorage;

public class DreamTripsAuthStorage extends AuthStorage<Session> {

   private final SessionHolder sessionHolder;

   public DreamTripsAuthStorage(SessionHolder sessionHolder) {
      super(Session.class);
      this.sessionHolder = sessionHolder;
   }

   @Override
   public void storeAuth(Session session) {
      sessionHolder.put(ImmutableUserSession.builder()
            .from(sessionHolder.get().get())
            .user(session.getUser())
            .locale(session.getLocale())
            .apiToken(session.getToken())
            .legacyApiToken(session.getSsoToken())
            .lastUpdate(System.currentTimeMillis())
            .permissions(session.getPermissions()).build());
   }
}
