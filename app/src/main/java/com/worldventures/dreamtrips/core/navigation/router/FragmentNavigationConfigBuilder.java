package com.worldventures.dreamtrips.core.navigation.router;

import android.os.Parcelable;
import android.support.v4.app.FragmentManager;

import com.worldventures.dreamtrips.R;

public class FragmentNavigationConfigBuilder extends NavigationConfigBuilder {

    FragmentNavigationConfigBuilder() {
        navigationConfig = new NavigationConfig(NavigationConfig.NavigationType.FRAGMENT);
    }

    /**
     * Default config includes enabled backstack and container ID set to "R.id.container_main"
     */
    @Override
    public NavigationConfigBuilder useDefaults() {
        navigationConfig = new NavigationConfig(NavigationConfig.NavigationType.FRAGMENT);
        navigationConfig.backStackEnabled = true;
        navigationConfig.containerId = R.id.container_main;
        return this;
    }

    public FragmentNavigationConfigBuilder data(Parcelable data) {
        super.data(data);
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
