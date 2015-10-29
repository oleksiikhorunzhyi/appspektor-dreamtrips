package com.worldventures.dreamtrips.core.navigation.router;

import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentManager;

import com.worldventures.dreamtrips.core.navigation.ActivityRouter;
import com.worldventures.dreamtrips.core.navigation.FragmentCompass;
import com.worldventures.dreamtrips.core.navigation.Route;
import com.worldventures.dreamtrips.modules.common.presenter.ComponentPresenter;

public class RouterImpl implements Router {

    private FragmentActivity activity;

    public RouterImpl(FragmentActivity activity) {
        this.activity = activity;
    }

    @Override
    public void moveTo(Route route, NavigationConfig config) {
        if (config.getNavigationType() == NavigationConfig.NavigationType.ACTIVITY) {
            openActivity(route, config);
        } else {
            openFragment(route, config);
        }
    }

    @Override
    public void moveTo(Route route) {
        openActivity(route, NavigationConfig.Builder.forActivity().build());
    }

    private void openActivity(Route route, NavigationConfig config) {
        ActivityRouter activityRouter = new ActivityRouter(activity);
        activityRouter.openComponentActivity(route, getArgs(config));
    }

    private void openFragment(Route route, NavigationConfig config) {
        FragmentManager fragmentManager = config.getFragmentManager() == null ?
                activity.getSupportFragmentManager() :
                config.getFragmentManager();
        //
        FragmentCompass fragmentCompass = new FragmentCompass(activity);
        fragmentCompass.setContainerId(config.getContainerId());
        fragmentCompass.setSupportFragmentManager(fragmentManager);
        fragmentCompass.setBackStackEnabled(config.isBackStackEnabled());
        fragmentCompass.replace(route, getArgs(config));
    }

    private Bundle getArgs(NavigationConfig config) {
        Bundle args = new Bundle();
        args.putParcelable(ComponentPresenter.EXTRA_DATA, config.getData());
        return args;
    }
}
