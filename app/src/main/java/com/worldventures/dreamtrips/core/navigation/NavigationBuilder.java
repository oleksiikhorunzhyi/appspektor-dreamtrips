package com.worldventures.dreamtrips.core.navigation;

import android.os.Bundle;
import android.os.Parcelable;
import android.support.v4.app.FragmentManager;

import com.worldventures.dreamtrips.modules.common.presenter.ComponentPresenter;

/**
 * Use {@link com.worldventures.dreamtrips.core.navigation.router.Router} interface
 */
@Deprecated
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

    public NavigationBuilder forDialog(FragmentManager fm) {
        navigator = new DialogFragmentNavigator(fm);
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
        checkNavigatorState();
        navigator.move(route, getArgs());
    }

    public void attach(Route route) {
        checkNavigatorState();
        navigator.attach(route, getArgs());
    }

    private void checkNavigatorState() {
        if (navigator == null) {
            throw new IllegalStateException("navigator can't be null at this point! Maybe you've" +
                    "forgot to set FragmentCompass, ActivityRouter or FragmentManager(for dialogs) before.");
        }
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
