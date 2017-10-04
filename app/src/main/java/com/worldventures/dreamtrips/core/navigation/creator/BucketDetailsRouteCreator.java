package com.worldventures.dreamtrips.core.navigation.creator;

import com.worldventures.core.model.session.SessionHolder;
import com.worldventures.core.model.session.UserSession;
import com.worldventures.core.storage.complex_objects.Optional;
import com.worldventures.dreamtrips.core.navigation.Route;

public class BucketDetailsRouteCreator implements RouteCreator<Integer> {

   private SessionHolder appSessionHolder;

   public BucketDetailsRouteCreator(SessionHolder appSessionHolder) {
      this.appSessionHolder = appSessionHolder;
   }

   @Override
   public Route createRoute(Integer arg) {
      Optional<UserSession> userSessionOptional = appSessionHolder.get();
      if (userSessionOptional.isPresent()) {
         if (arg == null || arg.intValue() == userSessionOptional.get().getUser().getId()) {
            return Route.DETAIL_BUCKET;
         } else {
            return Route.DETAIL_FOREIGN_BUCKET;
         }
      }
      return null;
   }
}
