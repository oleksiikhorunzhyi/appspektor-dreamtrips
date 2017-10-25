package com.worldventures.dreamtrips.core.janet.api_lib;

import com.worldventures.core.model.session.SessionHolder;
import com.worldventures.dreamtrips.mobilesdk.authentication.AuthDataProvider;

public class MonolithAuthDataProvider implements AuthDataProvider<MonolithAuthData> {

   private final SessionHolder sessionHolder;

   public MonolithAuthDataProvider(SessionHolder sessionHolder) {
      this.sessionHolder = sessionHolder;
   }

   @Override
   public MonolithAuthData data() {

      return ImmutableMonolithAuthData.builder()
            .token(sessionHolder.get().get().getApiToken())
            .build();
   }
}
