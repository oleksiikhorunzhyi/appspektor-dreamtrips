package com.worldventures.core.modules.auth.util;

import com.worldventures.core.model.Session;
import com.worldventures.core.model.session.ImmutableUserSession;
import com.worldventures.core.model.session.SessionHolder;
import com.worldventures.core.model.session.UserSession;
import com.worldventures.core.storage.complex_objects.Optional;

public final class SessionUtil {

   private SessionUtil() {
   }

   public static UserSession createUserSession(Session session, String userName, String userPassword) {
      return ImmutableUserSession.builder()
            .user(session.getUser())
            .locale(session.getLocale())
            .apiToken(session.getToken())
            .legacyApiToken(session.getSsoToken())
            .username(userName)
            .userPassword(userPassword)
            .lastUpdate(System.currentTimeMillis())
            .addAllPermissions(session.getPermissions())
            .build();
   }

   public static boolean isUserSessionTokenExist(SessionHolder sessionHolder) {
      Optional<UserSession> session = sessionHolder.get();
      return session.isPresent() && session.get().apiToken() != null;
   }
}
