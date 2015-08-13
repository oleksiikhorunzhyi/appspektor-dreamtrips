package com.worldventures.dreamtrips.core.navigation;

import android.os.Bundle;

//TODO make activity router fix ActivityRouter
public class ActivityNavigator implements Navigator {

    private ActivityRouter activityRouter;

    public ActivityNavigator(ActivityRouter activityRouter) {
        this.activityRouter = activityRouter;
    }

    @Override
    public void move(Route route) {
        if (route.equals(Route.DETAIL_BUCKET)) {
            activityRouter.openBucketItemDetails(null);
        }
    }

    @Override
    public void move(Route route, Bundle bundle) {
        if (route.equals(Route.DETAIL_BUCKET)) {
            activityRouter.openBucketItemDetails(bundle);
        }
    }
}
