package com.worldventures.dreamtrips.core.navigation;

import android.os.Bundle;

@Deprecated
public class FragmentNavigator implements Navigator {

    private FragmentCompass fragmentCompass;

    public FragmentNavigator(FragmentCompass fragmentCompass) {
        this.fragmentCompass = fragmentCompass;
    }

    @Override
    public void move(Route route, Bundle bundle) {
        fragmentCompass.switchBranch(route, bundle);
    }

    @Override
    public void attach(Route route, Bundle bundle) {
        fragmentCompass.add(route, bundle);
    }
}
