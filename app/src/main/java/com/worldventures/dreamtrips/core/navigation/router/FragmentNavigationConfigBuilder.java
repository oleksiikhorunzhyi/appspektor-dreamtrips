package com.worldventures.dreamtrips.core.navigation.router;

import android.os.Parcelable;
import android.support.annotation.IdRes;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;

import com.worldventures.dreamtrips.R;

public class FragmentNavigationConfigBuilder extends NavigationConfigBuilder {

    FragmentNavigationConfigBuilder() {
        super(NavigationConfig.NavigationType.FRAGMENT);
    }

    /**
     * Default config includes enabled backstack, clearBackStack = 'false' and container ID set to
     * {@link R.id#container_main}
     */
    @Override
    public FragmentNavigationConfigBuilder useDefaults() {
        navigationConfig.backStackEnabled = true;
        navigationConfig.containerId = R.id.container_main;
        navigationConfig.clearBackStack = false;
        return this;
    }

    public FragmentNavigationConfigBuilder data(Parcelable data) {
        super.data(data);
        return this;
    }

    public FragmentNavigationConfigBuilder containerId(@IdRes int containerId) {
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

    public FragmentNavigationConfigBuilder targetFragment(Fragment fragment) {
        navigationConfig.targetFragment = fragment;
        return this;
    }

    /**
     * Will clear stack of given FragmentManager before new transaction.<br />
     * Default is <b><u> false</u></b>
     */
    public FragmentNavigationConfigBuilder clearBackStack(boolean clearBackStack) {
        navigationConfig.clearBackStack = clearBackStack;
        return this;
    }

    @Override
    protected void validateConfig() throws IllegalStateException {
        StringBuilder reasonBuilder = new StringBuilder("Navigation config corrupted state:\n");
        boolean corrupted = false;
        if (navigationConfig.containerId == 0) {
            reasonBuilder.append("containerId = 0\n");
            corrupted = true;
        }
        if (navigationConfig.backStackEnabled == null) {
            reasonBuilder.append("backStackEnabled is null\n");
            corrupted = true;
        }
        if (navigationConfig.clearBackStack == null) {
            reasonBuilder.append("clearBackStack is null\n");
            corrupted = true;
        }
        if (corrupted) throw new IllegalStateException(reasonBuilder.toString());
    }
}
