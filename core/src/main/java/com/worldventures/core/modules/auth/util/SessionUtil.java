package com.worldventures.core.modules.auth.util;

import com.worldventures.core.model.Session;
import com.worldventures.core.model.session.ImmutableUserSession;
import com.worldventures.core.model.session.SessionHolder;
import com.worldventures.core.model.session.UserSession;

public class SessionUtil {

   public static UserSession createUserSession(Session session, String userName, String userPassword) {
      return ImmutableUserSession.builder()
            .username(userName)
            .userPassword(userPassword)
            .locale(session.getLocale())
            .user(session.getUser())
            .apiToken(session.getToken())
            .legacyApiToken(session.getSsoToken())
            .lastUpdate(System.currentTimeMillis())
            .permissions(session.getPermissions()).build();
   }

   public static boolean isUserSessionTokenExist(SessionHolder sessionHolder) {
      try {
         UserSession userSession = sessionHolder.get().isPresent() ? sessionHolder.get().get() : null;
         return userSession != null && userSession.apiToken() != null;
      } catch (Exception ex) {
         return false;
      }
   }
}
