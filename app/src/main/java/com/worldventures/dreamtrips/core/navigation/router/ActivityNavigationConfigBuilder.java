package com.worldventures.dreamtrips.core.navigation.router;

import android.os.Parcelable;

import com.worldventures.dreamtrips.core.navigation.ToolbarConfig;

public class ActivityNavigationConfigBuilder extends NavigationConfigBuilder {

    ActivityNavigationConfigBuilder() {
        navigationConfig = new NavigationConfig(NavigationConfig.NavigationType.ACTIVITY);
    }

    /**
     * Default config includes no specific setup when routing to activity
     */
    @Override
    public NavigationConfigBuilder useDefaults() {
        navigationConfig = new NavigationConfig(NavigationConfig.NavigationType.ACTIVITY);
        return this;
    }

    public ActivityNavigationConfigBuilder data(Parcelable data) {
        super.data(data);
        return this;
    }

    public ActivityNavigationConfigBuilder toolbarConfig(ToolbarConfig config) {
        navigationConfig.toolbarConfig = config;
        return this;
    }
}
