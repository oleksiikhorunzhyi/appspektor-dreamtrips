package com.worldventures.dreamtrips.modules.auth.util;

import com.techery.spares.session.SessionHolder;
import com.worldventures.dreamtrips.core.session.UserSession;
import com.worldventures.dreamtrips.modules.common.model.Session;

public class SessionUtil {

   public static UserSession createUserSession(Session session, String userName, String userPassword) {
      UserSession userSession = new UserSession();
      userSession.setUser(session.getUser());
      userSession.setApiToken(session.getToken());
      userSession.setLegacyApiToken(session.getSsoToken());
      userSession.setUsername(userName);
      userSession.setUserPassword(userPassword);
      userSession.setLocale(session.getLocale());
      userSession.setLastUpdate(System.currentTimeMillis());
      userSession.setFeatures(session.getPermissions());
      return userSession;
   }

   public static boolean isUserSessionTokenExist(SessionHolder<UserSession> sessionHolder) {
      try {
         UserSession userSession = sessionHolder.get().isPresent() ? sessionHolder.get().get() : null;
         return userSession != null && userSession.getApiToken() != null;
      } catch (Exception ex) {
         return false;
      }
   }
}
