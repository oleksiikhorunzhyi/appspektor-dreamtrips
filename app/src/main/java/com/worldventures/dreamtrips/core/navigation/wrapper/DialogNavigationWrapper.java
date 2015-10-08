package com.worldventures.dreamtrips.core.navigation.wrapper;

import android.os.Parcelable;
import android.support.v4.app.FragmentManager;

import com.worldventures.dreamtrips.core.navigation.NavigationBuilder;
import com.worldventures.dreamtrips.core.navigation.Route;

public class DialogNavigationWrapper extends NavigationWrapper {

    private final FragmentManager fragmentManager;

    public DialogNavigationWrapper(FragmentManager fragmentManager) {
        super(NavigationBuilder.create());
        this.fragmentManager = fragmentManager;
    }

    @Override
    public void navigate(Route route, Parcelable bundle) {
        navigationBuilder
                .forDialog(fragmentManager)
                .data(bundle)
                .move(route);
    }
}
