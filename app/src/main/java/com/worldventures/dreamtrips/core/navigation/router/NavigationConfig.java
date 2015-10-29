package com.worldventures.dreamtrips.core.navigation.router;

import android.os.Parcelable;
import android.support.annotation.Nullable;
import android.support.v4.app.FragmentManager;

import com.worldventures.dreamtrips.R;
import com.worldventures.dreamtrips.core.navigation.ToolbarConfig;

public class NavigationConfig {

    NavigationType navigationType;
    Parcelable data;
    FragmentManager fragmentManager;
    int containerId = R.id.container_main;
    boolean backStackEnabled = true;
    ToolbarConfig toolbarConfig;

    NavigationConfig(NavigationType type) {
        navigationType = type;
    }

    public Parcelable getData() {
        return data;
    }

    @Nullable
    public FragmentManager getFragmentManager() {
        return fragmentManager;
    }

    public int getContainerId() {
        return containerId;
    }

    public boolean isBackStackEnabled() {
        return backStackEnabled;
    }

    public NavigationType getNavigationType() {
        return navigationType;
    }

    @Nullable
    public ToolbarConfig getToolbarConfig() {
        return toolbarConfig;
    }

    enum NavigationType {
        ACTIVITY, FRAGMENT
    }
}
