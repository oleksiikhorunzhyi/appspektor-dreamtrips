package com.worldventures.dreamtrips.core.navigation;

import android.os.Bundle;

public class ActivityNavigator implements Navigator {

    private ActivityRouter activityRouter;

    public ActivityNavigator(ActivityRouter activityRouter) {
        this.activityRouter = activityRouter;
    }

    @Override
    public void move(Route route, Bundle bundle) {
        activityRouter.openComponentActivity(route, bundle);
    }

    @Override
    public void attach(Route route, Bundle bundle) {
        activityRouter.openComponentActivity(route, bundle);
    }

}
