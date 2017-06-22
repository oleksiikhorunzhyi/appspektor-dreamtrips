package com.worldventures.dreamtrips.core.janet.api_lib;

import com.techery.spares.session.SessionHolder;
import com.worldventures.dreamtrips.core.session.UserSession;
import com.worldventures.dreamtrips.mobilesdk.authentication.AuthDataProvider;

public class MonolithAuthDataProvider implements AuthDataProvider<MonolithAuthData> {

   private final SessionHolder<UserSession> sessionHolder;

   public MonolithAuthDataProvider(SessionHolder<UserSession> sessionHolder) {
      this.sessionHolder = sessionHolder;
   }

   @Override
   public MonolithAuthData data() {

      return ImmutableMonolithAuthData.builder()
            .token(sessionHolder.get().get().getApiToken())
            .build();
   }
}
