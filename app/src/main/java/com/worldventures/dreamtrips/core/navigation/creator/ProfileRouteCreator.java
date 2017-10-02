package com.worldventures.dreamtrips.core.navigation.creator;

import com.worldventures.core.model.session.SessionHolder;
import com.worldventures.core.model.session.UserSession;
import com.worldventures.core.storage.complex_objects.Optional;
import com.worldventures.dreamtrips.core.navigation.Route;

public class ProfileRouteCreator implements RouteCreator<Integer> {

   private SessionHolder appSessionHolder;

   public ProfileRouteCreator(SessionHolder appSessionHolder) {
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
