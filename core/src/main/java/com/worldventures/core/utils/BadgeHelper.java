package com.worldventures.core.utils;

import com.innahema.collections.query.queriables.Queryable;
import com.worldventures.core.model.User;
import com.worldventures.core.model.session.SessionHolder;
import com.worldventures.core.model.session.UserSession;

public class BadgeHelper {

   private static final String WV_PROSPECT = "WVProspect";
   private static final String TRIP_CHAT_HOST = "DreamTrips Host";

   private SessionHolder sessionHolder;

   public BadgeHelper(SessionHolder sessionHolder) {
      this.sessionHolder = sessionHolder;
   }

   public boolean isWVProspect() {
      UserSession userSession = sessionHolder.get().orNull();

      return !(userSession == null || userSession.getUser() == null || userSession.getUser().getBadges() == null)
            && Queryable.from(userSession.getUser().getBadges()).any(userBadge -> userBadge.equals(WV_PROSPECT));
   }

   public boolean hasTripChatHost(User user) {
      return user.getBadges() != null && Queryable.from(user.getBadges()).any(userBadge -> userBadge.equals(TRIP_CHAT_HOST));
   }
}
