package com.worldventures.dreamtrips.core.janet.api_lib;

import com.worldventures.core.model.Session;
import com.worldventures.core.model.User;
import com.worldventures.core.model.session.SessionHolder;
import com.worldventures.core.model.session.UserSession;

public class DreamTripsAuthStorage extends AuthStorage<Session> {

   private final SessionHolder sessionHolder;

   public DreamTripsAuthStorage(SessionHolder sessionHolder) {
      super(Session.class);
      this.sessionHolder = sessionHolder;
   }

   @Override
   public void storeAuth(Session session) {
      User sessionUser = session.getUser();
      UserSession userSession = new UserSession();
      if (sessionHolder.get().isPresent()) {
         userSession = sessionHolder.get().get();
      }
      userSession.setLocale(session.getLocale());
      userSession.setUser(sessionUser);
      userSession.setApiToken(session.getToken());
      userSession.setLegacyApiToken(session.getSsoToken());

      userSession.setLastUpdate(System.currentTimeMillis());

      userSession.setFeatures(session.getPermissions());

      sessionHolder.put(userSession);
   }
}
