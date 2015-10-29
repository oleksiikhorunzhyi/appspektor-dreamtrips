package com.worldventures.dreamtrips.core.navigation.router;

public abstract class NavigationConfigBuilder {

    protected NavigationConfig navigationConfig;

    public static ActivityNavigationConfigBuilder forActivity() {
        return new ActivityNavigationConfigBuilder();
    }

    public static FragmentNavigationConfigBuilder forFragment() {
        return new FragmentNavigationConfigBuilder();
    }

    public NavigationConfig build() {
        return navigationConfig;
    }
}
