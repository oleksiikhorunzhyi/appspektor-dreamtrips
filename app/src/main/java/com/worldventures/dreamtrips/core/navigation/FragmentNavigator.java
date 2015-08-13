package com.worldventures.dreamtrips.core.navigation;

import android.os.Bundle;

public class FragmentNavigator implements Navigator {

    private FragmentCompass fragmentCompass;

    public FragmentNavigator(FragmentCompass fragmentCompass) {
        this.fragmentCompass = fragmentCompass;
    }

    @Override
    public void move(Route route) {
        fragmentCompass.replace(route);
    }

    @Override
    public void move(Route route, Bundle bundle) {
        fragmentCompass.replace(route, bundle);
    }
}
