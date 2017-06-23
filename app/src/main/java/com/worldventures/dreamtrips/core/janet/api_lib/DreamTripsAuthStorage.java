package com.worldventures.dreamtrips.core.janet.api_lib;

import com.techery.spares.session.SessionHolder;
import com.worldventures.dreamtrips.core.session.UserSession;
import com.worldventures.dreamtrips.core.session.acl.Feature;
import com.worldventures.dreamtrips.modules.common.model.Session;
import com.worldventures.dreamtrips.modules.common.model.User;

import java.util.List;

public class DreamTripsAuthStorage extends AuthStorage<Session> {

   private final SessionHolder<UserSession> sessionHolder;

   public DreamTripsAuthStorage(SessionHolder<UserSession> sessionHolder) {
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

      List<Feature> features = session.getPermissions();
      userSession.setFeatures(features);

      sessionHolder.put(userSession);
   }
}
