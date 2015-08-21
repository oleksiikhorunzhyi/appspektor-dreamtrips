package com.worldventures.dreamtrips.core.navigation;

import android.os.Bundle;

import com.worldventures.dreamtrips.modules.common.presenter.ComponentPresenter;

public class NavigationBuilder {

    Navigator navigator;
    Bundle args;
    ToolbarConfig toolbarConfig;

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

    public NavigationBuilder toolbarConfig(ToolbarConfig toolbarConfig) {
        this.toolbarConfig = toolbarConfig;
        return this;
    }

    public void move(Route route) {
        navigator.move(route, getArgs());
    }

    public void attach(Route route) {
        navigator.attach(route, getArgs());
    }

    private Bundle getArgs() {
        if (args == null) {
            args = new Bundle();
        }
        args.putSerializable(ComponentPresenter.COMPONENT_TOOLBAR_CONFIG, toolbarConfig);
        return args;
    }
}
