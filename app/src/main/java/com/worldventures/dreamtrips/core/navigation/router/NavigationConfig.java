package com.worldventures.dreamtrips.core.navigation.router;

import android.os.Parcelable;
import android.support.v4.app.FragmentManager;

import com.worldventures.dreamtrips.R;

public class NavigationConfig {

    private NavigationType navigationType;

    private Parcelable data;

    private FragmentManager fragmentManager;
    private int containerId = R.id.container_main;
    private boolean backStackEnabled;

    private NavigationConfig(NavigationType navigationType) {
        this.navigationType = navigationType;
    }

    public NavigationType getNavigationType() {
        return navigationType;
    }

    public Parcelable getData() {
        return data;
    }

    public FragmentManager getFragmentManager() {
        return fragmentManager;
    }

    public int getContainerId() {
        return containerId;
    }

    public boolean isBackStackEnabled() {
        return backStackEnabled;
    }

    enum NavigationType {
        ACTIVITY, FRAGMENT
    }

    public static class Builder {
        NavigationConfig navigationConfig;

        private Builder(NavigationType navigationType) {
            navigationConfig = new NavigationConfig(navigationType);
        }

        public static Builder forFragment() {
            return new Builder(NavigationType.FRAGMENT);
        }

        public static Builder forActivity() {
            return new Builder(NavigationType.ACTIVITY);
        }

        public Builder data(Parcelable data) {
            navigationConfig.data = data;
            return this;
        }

        public Builder containerId(int containerId) {
            navigationConfig.containerId = containerId;
            return this;
        }

        public Builder fragmentManager(FragmentManager fragmentManager) {
            navigationConfig.fragmentManager = fragmentManager;
            return this;
        }

        public Builder backStackEnabled(boolean backStackEnabled) {
            navigationConfig.backStackEnabled = backStackEnabled;
            return this;
        }

        public NavigationConfig build() {
            return navigationConfig;
        }
    }

}
