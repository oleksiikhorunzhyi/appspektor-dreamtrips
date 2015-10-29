package com.worldventures.dreamtrips.core.navigation.router;

import android.os.Parcelable;
import android.support.v4.app.FragmentManager;

public class FragmentNavigationConfigBuilder extends NavigationConfigBuilder {

    FragmentNavigationConfigBuilder() {
        navigationConfig = new NavigationConfig(NavigationConfig.NavigationType.FRAGMENT);
    }

    public FragmentNavigationConfigBuilder data(Parcelable data) {
        navigationConfig.data = data;
        return this;
    }

    public FragmentNavigationConfigBuilder containerId(int containerId) {
        navigationConfig.containerId = containerId;
        return this;
    }

    public FragmentNavigationConfigBuilder fragmentManager(FragmentManager fragmentManager) {
        navigationConfig.fragmentManager = fragmentManager;
        return this;
    }

    public FragmentNavigationConfigBuilder backStackEnabled(boolean backStackEnabled) {
        navigationConfig.backStackEnabled = backStackEnabled;
        return this;
    }
}
