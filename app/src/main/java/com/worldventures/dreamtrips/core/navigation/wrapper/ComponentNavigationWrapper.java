package com.worldventures.dreamtrips.core.navigation.wrapper;

import android.os.Parcelable;

import com.worldventures.dreamtrips.core.navigation.ActivityRouter;
import com.worldventures.dreamtrips.core.navigation.NavigationBuilder;
import com.worldventures.dreamtrips.core.navigation.Route;

public class ComponentNavigationWrapper extends NavigationWrapper {

    private final ActivityRouter activityRouter;

    public ComponentNavigationWrapper(ActivityRouter activityRouter) {
        super(NavigationBuilder.create());
        this.activityRouter = activityRouter;
    }

    @Override
    public void navigate(Route route, Parcelable bundle) {
        navigationBuilder
                .with(activityRouter)
                .data(bundle)
                .move(route);
    }
}
