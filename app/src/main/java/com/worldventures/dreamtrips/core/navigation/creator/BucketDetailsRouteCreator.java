package com.worldventures.dreamtrips.core.navigation.creator;

import com.techery.spares.session.SessionHolder;
import com.techery.spares.storage.complex_objects.Optional;
import com.worldventures.dreamtrips.core.navigation.Route;
import com.worldventures.dreamtrips.core.session.UserSession;

public class BucketDetailsRouteCreator implements RouteCreator<Integer> {

    private SessionHolder<UserSession> appSessionHolder;

    public BucketDetailsRouteCreator(SessionHolder<UserSession> appSessionHolder) {
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
