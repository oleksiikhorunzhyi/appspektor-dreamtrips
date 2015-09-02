package com.worldventures.dreamtrips.core.navigation;

import android.os.Bundle;
import android.os.Parcelable;

import com.worldventures.dreamtrips.modules.common.presenter.ComponentPresenter;

public class NavigationBuilder {

    Navigator navigator;
    Bundle args;
    ToolbarConfig toolbarConfig;
    Parcelable data;

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

    @Deprecated
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

    public NavigationBuilder data(Parcelable data) {
        this.data = data;
        return this;
    }

    private Bundle getArgs() {
        if (args == null) {
            args = new Bundle();
        }
        args.putSerializable(ComponentPresenter.COMPONENT_TOOLBAR_CONFIG, toolbarConfig);
        args.putParcelable(ComponentPresenter.EXTRA_DATA, data);
        return args;
    }


}
