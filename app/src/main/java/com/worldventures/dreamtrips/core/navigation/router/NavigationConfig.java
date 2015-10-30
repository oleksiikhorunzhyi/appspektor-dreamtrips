package com.worldventures.dreamtrips.core.navigation.router;

import android.os.Parcelable;
import android.support.annotation.Nullable;
import android.support.v4.app.FragmentManager;

import com.worldventures.dreamtrips.core.navigation.ToolbarConfig;

public class NavigationConfig {

    NavigationType navigationType;
    Parcelable data;
    FragmentManager fragmentManager;
    ToolbarConfig toolbarConfig;
    int containerId;
    Boolean backStackEnabled;

    NavigationConfig(NavigationType type) {
        navigationType = type;
    }

    public NavigationType getNavigationType() {
        return navigationType;
    }

    public Parcelable getData() {
        return data;
    }

    @Nullable
    public FragmentManager getFragmentManager() {
        return fragmentManager;
    }

    @Nullable
    public ToolbarConfig getToolbarConfig() {
        return toolbarConfig;
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
}
