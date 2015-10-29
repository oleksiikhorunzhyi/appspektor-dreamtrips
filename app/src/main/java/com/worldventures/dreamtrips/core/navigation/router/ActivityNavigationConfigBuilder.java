package com.worldventures.dreamtrips.core.navigation.router;

import android.os.Parcelable;

import com.worldventures.dreamtrips.core.navigation.ToolbarConfig;

public class ActivityNavigationConfigBuilder extends NavigationConfigBuilder {

    ActivityNavigationConfigBuilder() {
        navigationConfig = new NavigationConfig(NavigationConfig.NavigationType.ACTIVITY);
    }

    public ActivityNavigationConfigBuilder data(Parcelable data) {
        navigationConfig.data = data;
        return this;
    }

    public ActivityNavigationConfigBuilder toolbarConfig(ToolbarConfig config) {
        navigationConfig.toolbarConfig = config;
        return this;
    }
}
