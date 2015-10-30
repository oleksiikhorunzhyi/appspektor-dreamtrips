package com.worldventures.dreamtrips.core.navigation.router;

import android.os.Parcelable;

public abstract class NavigationConfigBuilder {

    protected NavigationConfig navigationConfig;

    public static ActivityNavigationConfigBuilder forActivity() {
        return new ActivityNavigationConfigBuilder();
    }

    public static FragmentNavigationConfigBuilder forFragment() {
        return new FragmentNavigationConfigBuilder();
    }

    public NavigationConfigBuilder data(Parcelable data) {
        navigationConfig.data = data;
        return this;
    }

    /**
     * Create default builder and build it - for cases when we need no customization
     */
    public NavigationConfig buildDefault() {
        return useDefaults().build();
    }

    /**
     * Use some default config - this is specific for every navigation type and will be overridden
     */
    public abstract NavigationConfigBuilder useDefaults();

    public NavigationConfig build() {
        validateConfigState();
        return navigationConfig;
    }

    protected void validateConfigState() throws IllegalStateException {
        String reason = "Navigation config corrupted state:\n";
        boolean corrupted = false;
        if (navigationConfig.navigationType.equals(NavigationConfig.NavigationType.FRAGMENT)) {
            if (navigationConfig.containerId == 0) {
                reason += "containerId = 0\n";
                corrupted = true;
            }
            if (navigationConfig.backStackEnabled == null) {
                reason += "backStackEnabled is null\n";
                corrupted = true;
            }
        }
        if (corrupted) throw new IllegalStateException(reason);
    }
}
