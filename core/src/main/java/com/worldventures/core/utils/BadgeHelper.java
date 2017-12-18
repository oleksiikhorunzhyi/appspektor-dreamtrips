package com.worldventures.core.utils;

import com.innahema.collections.query.queriables.Queryable;
import com.worldventures.core.model.User;
import com.worldventures.core.model.session.SessionHolder;
import com.worldventures.core.model.session.UserSession;

public class BadgeHelper {
   public static final boolean WV_PROSPECT_CHECK_DISABLED = true;

   private static final String WV_PROSPECT = "WVProspect";
   private static final String TRIP_CHAT_HOST = "DreamTrips Host";

   private final SessionHolder sessionHolder;

   public BadgeHelper(SessionHolder sessionHolder) {
      this.sessionHolder = sessionHolder;
   }

   public boolean isWVProspect() {
      if (WV_PROSPECT_CHECK_DISABLED) {
         return false;
      }
      UserSession userSession = sessionHolder.get().orNull();

      return !(userSession == null || userSession.user() == null || userSession.user().getBadges() == null)
         && Queryable.from(userSession.user().getBadges()).any(userBadge -> userBadge.equals(WV_PROSPECT));
   }

   public boolean hasTripChatHost(User user) {
      return user.getBadges() != null && Queryable.from(user.getBadges()).any(userBadge -> userBadge.equals(TRIP_CHAT_HOST));
   }
}
