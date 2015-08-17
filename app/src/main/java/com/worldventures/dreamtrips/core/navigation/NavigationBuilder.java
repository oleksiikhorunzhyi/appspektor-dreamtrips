package com.worldventures.dreamtrips.core.navigation;

import android.os.Bundle;

public class NavigationBuilder {

    Navigator navigator;
    Bundle args;

    public static NavigationBuilder create() {
        return new NavigationBuilder();
    }

    public NavigationBuilder with(FragmentCompass fragmentCompass) {
        navigator = new FragmentNavigator(fragmentCompass);
        return this;
    }

    public NavigationBuilder with(ActivityRouter activityRouter) {
        navigator = new ActivityNavigator(activityRouter);
        return this;
    }

    public NavigationBuilder args(Bundle args) {
        this.args = args;
        return this;
    }

    public void move(Route route) {
        navigator.move(route, args);
    }

    public void attach(Route route) {
        navigator.attach(route, args);
    }
}
