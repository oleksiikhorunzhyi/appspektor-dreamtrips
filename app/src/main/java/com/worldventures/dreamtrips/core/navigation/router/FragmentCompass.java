package com.worldventures.dreamtrips.core.navigation.router;

import android.os.Bundle;
import android.support.v4.app.DialogFragment;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;

import com.worldventures.dreamtrips.core.component.ComponentDescription;
import com.worldventures.dreamtrips.core.navigation.Route;
import com.worldventures.dreamtrips.modules.common.view.fragment.BaseFragment;

import timber.log.Timber;

class FragmentCompass {

    private FragmentActivity activity;

    private int containerId;
    private boolean backStackEnabled = true;
    private FragmentManager fragmentManager;

    /**
     * This constructor is to be used with {@link com.worldventures.dreamtrips.core.navigation.router.Router Router}
     * and {@link com.worldventures.dreamtrips.core.navigation.router.NavigationConfig NavigationConfig} only!
     */
    public FragmentCompass(FragmentActivity activity) {
        this.activity = activity;
    }

    public void setContainerId(int containerId) {
        this.containerId = containerId;
    }

    public void add(Route route) {
        add(route, null);
    }

    public void add(Route route, Bundle bundle) {
        action(Action.ADD, route, bundle);
    }

    public void replace(ComponentDescription componentDescription) {
        replace(componentDescription, null);
    }

    public void replace(ComponentDescription componentDescription, Bundle args) {
        replace(Route.restoreByKey(componentDescription.getKey()), args);
    }

    public void replace(Route route) {
        replace(route, null);
    }

    public void replace(Route route, Bundle bundle) {
        action(Action.REPLACE, route, bundle);
    }

    public void remove(Route route) {
        remove(route.getClazzName());
    }

    public void remove(String name) {
        if (validateState()) {
            Fragment fragment = fragmentManager.findFragmentByTag(name);
            //
            if (fragment != null) {
                FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
                fragmentTransaction.remove(fragment);
                fragmentTransaction.commit();
            }
        }
    }

    public void setFragmentManager(FragmentManager fragmentManager) {
        this.fragmentManager = fragmentManager;
    }

    public FragmentManager getFragmentManager() {
        return fragmentManager;
    }

    protected void action(Action action, Route route, Bundle bundle) {
        if (!validateState()) {
            Timber.e(new IllegalStateException("Incorrect call of transaction manager action. validateState() false."), "");
        } else {
            String clazzName = route.getClazzName();
            //
            BaseFragment fragment = (BaseFragment) Fragment.instantiate(activity, clazzName);
            setArgsToFragment(fragment, bundle);
            FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
            //
            switch (action) {
                case REPLACE:
                    fragmentTransaction.replace(containerId, fragment, clazzName);
                    break;
                case ADD:
                    fragmentTransaction.add(containerId, fragment, clazzName);
                    break;
            }
            if (backStackEnabled) {
                fragmentTransaction.addToBackStack(route.name());
            }
            fragmentTransaction.commit();
        }
    }

    public void show(DialogFragment dialogFragment, String tag) {
        if (validateState()) {
            FragmentManager supportFragmentManager = activity.getSupportFragmentManager();
            dialogFragment.show(supportFragmentManager, tag);
        }
    }

    public void setBackStackEnabled(boolean enabled) {
        this.backStackEnabled = enabled;
    }

    private void setArgsToFragment(BaseFragment fragment, Bundle bundle) {
        fragment.setArguments(bundle);
    }

    private boolean validateState() {
        return activity != null && !activity.isFinishing();
    }

    public BaseFragment getCurrentFragment() {
        return (BaseFragment) activity.getSupportFragmentManager().findFragmentById(containerId);
    }

    public boolean empty() {
        return getCurrentFragment() == null;
    }

    public enum Action {
        ADD, REPLACE, POP
    }
}
