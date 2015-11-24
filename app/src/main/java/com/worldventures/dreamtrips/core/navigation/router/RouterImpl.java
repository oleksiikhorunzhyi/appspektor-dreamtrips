package com.worldventures.dreamtrips.core.navigation.router;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;

import com.worldventures.dreamtrips.core.navigation.ActivityRouter;
import com.worldventures.dreamtrips.core.navigation.DialogFragmentNavigator;
import com.worldventures.dreamtrips.core.navigation.FragmentCompass;
import com.worldventures.dreamtrips.core.navigation.Route;
import com.worldventures.dreamtrips.modules.common.presenter.ComponentPresenter;

import timber.log.Timber;

public class RouterImpl implements Router {

    private FragmentActivity activity;

    public RouterImpl(FragmentActivity activity) {
        this.activity = activity;
    }

    @Override
    public void moveTo(Route route, NavigationConfig config) {
        switch (config.getNavigationType()) {
            case ACTIVITY:
                openActivity(route, config);
                break;
            case FRAGMENT:
                openFragment(route, config);
                break;
            case DIALOG:
                showDialog(route, config);
                break;
            case REMOVE:
                remove(route, config);
        }
    }

    @Override
    public void moveTo(Route route) {
        openActivity(route, NavigationConfigBuilder.forActivity().build());
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
        if (config.getClearBackStack()) {
            try {
                fragmentManager.popBackStackImmediate(null, FragmentManager.POP_BACK_STACK_INCLUSIVE);
            } catch (IllegalStateException e) {
                Timber.e("TransitionManager error", e); //for avoid application crash when called at runtime
            }
        }
        FragmentCompass fragmentCompass = new FragmentCompass(activity);
        fragmentCompass.setContainerId(config.getContainerId());
        fragmentCompass.setFragmentManager(fragmentManager);
        fragmentCompass.setBackStackEnabled(config.isBackStackEnabled());
        fragmentCompass.replace(route, getArgs(config));
    }

    private void showDialog(Route route, NavigationConfig config) {
        FragmentManager fragmentManager = config.getFragmentManager() == null ?
                activity.getSupportFragmentManager() :
                config.getFragmentManager();
        //
        DialogFragmentNavigator.NavigationDialogFragment.newInstance(route, getArgs(config))
                .show(fragmentManager, route.name());
    }

    private void remove(Route route, NavigationConfig config) {
        if (validateState()) {
            Fragment fragment = config.getFragmentManager().findFragmentByTag(route.getClazzName());
            //
            if (fragment != null) {
                FragmentTransaction fragmentTransaction = config.getFragmentManager().beginTransaction();
                fragmentTransaction.remove(fragment);
                fragmentTransaction.commit();
            }
        }
    }

    private boolean validateState() {
        return activity != null && !activity.isFinishing();
    }

    private Bundle getArgs(NavigationConfig config) {
        Bundle args = new Bundle();
        args.putParcelable(ComponentPresenter.EXTRA_DATA, config.getData());
        if (config.getToolbarConfig() != null) {
            args.putSerializable(ComponentPresenter.COMPONENT_TOOLBAR_CONFIG, config.getToolbarConfig());
        }
        return args;
    }
}
