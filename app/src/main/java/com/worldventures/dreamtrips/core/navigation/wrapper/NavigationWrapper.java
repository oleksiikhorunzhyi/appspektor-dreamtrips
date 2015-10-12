package com.worldventures.dreamtrips.core.navigation.wrapper;

import android.os.Parcelable;

import com.worldventures.dreamtrips.core.navigation.NavigationBuilder;
import com.worldventures.dreamtrips.core.navigation.Route;

public abstract class NavigationWrapper {
    protected final NavigationBuilder navigationBuilder;

    public NavigationWrapper(NavigationBuilder navigationBuilder) {
        this.navigationBuilder = navigationBuilder;
    }

    public abstract void navigate(Route route, Parcelable bundle);
}
