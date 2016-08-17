package com.worldventures.dreamtrips.core.navigation.creator;

import com.techery.spares.session.SessionHolder;
import com.techery.spares.storage.complex_objects.Optional;
import com.worldventures.dreamtrips.core.navigation.Route;
import com.worldventures.dreamtrips.core.session.UserSession;

public class ProfileRouteCreator implements RouteCreator<Integer> {

   SessionHolder<UserSession> appSessionHolder;

   public ProfileRouteCreator(SessionHolder<UserSession> appSessionHolder) {
      this.appSessionHolder = appSessionHolder;
   }

   @Override
   public Route createRoute(Integer arg) {
      Optional<UserSession> userSessionOptional = appSessionHolder.get();
      if (userSessionOptional.isPresent()) {
         if (arg == userSessionOptional.get().getUser().getId()) {
            return Route.ACCOUNT_PROFILE;
         } else {
            return Route.FOREIGN_PROFILE;
         }
      }
      return null;
   }
}
