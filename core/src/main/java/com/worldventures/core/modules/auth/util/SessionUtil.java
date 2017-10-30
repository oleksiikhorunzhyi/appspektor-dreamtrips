package com.worldventures.core.modules.auth.util;

import com.worldventures.core.model.Session;
import com.worldventures.core.model.session.SessionHolder;
import com.worldventures.core.model.session.UserSession;

public final class SessionUtil {

   private SessionUtil() {
   }

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

   public static boolean isUserSessionTokenExist(SessionHolder sessionHolder) {
      try {
         UserSession userSession = sessionHolder.get().isPresent() ? sessionHolder.get().get() : null; //NOPMD
         return userSession != null && userSession.getApiToken() != null;
      } catch (Exception ex) {
         return false;
      }
   }
}
