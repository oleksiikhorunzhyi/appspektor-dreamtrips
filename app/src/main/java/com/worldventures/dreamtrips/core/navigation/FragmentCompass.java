package com.worldventures.dreamtrips.core.navigation;

import android.os.Bundle;
import android.support.v4.app.DialogFragment;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.view.View;

import com.worldventures.dreamtrips.core.component.ComponentDescription;

import butterknife.ButterKnife;
import timber.log.Timber;

public class FragmentCompass {

    private FragmentActivity activity;

    private int containerId;
    private boolean backStackEnabled = true;
    private FragmentManager fragmentManager;

    /**
     * Deprecated in favor of {@link com.worldventures.dreamtrips.core.navigation.router.Router Router}
     * and {@link com.worldventures.dreamtrips.core.navigation.router.NavigationConfig NavigationConfig} scheme.
     */
    @Deprecated
    public FragmentCompass(FragmentActivity activity, int containerId) {
        this.activity = activity;
        this.containerId = containerId;
        fragmentManager = activity.getSupportFragmentManager();
    }

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

    public void showContainer() {
        View container = ButterKnife.findById(activity, containerId);
        if (container != null) container.setVisibility(View.VISIBLE);
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

    public void removeDetailed() {
        remove(Route.DETAIL_BUCKET.getClazzName());
    }

    public void removePost() {
        remove(Route.POST_CREATE.getClazzName());
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
            Fragment fragment = Fragment.instantiate(activity, clazzName);
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

    @Deprecated
    public void disableBackStack() {
        backStackEnabled = false;
    }

    @Deprecated
    public void enableBackStack() {
        backStackEnabled = true;
    }

    protected void clearBackStack() {
        try {
            fragmentManager.popBackStackImmediate(null, FragmentManager.POP_BACK_STACK_INCLUSIVE);
        } catch (IllegalStateException e) {
            Timber.e("TransitionManager error", e); //for avoid application crash when called at runtime
        }
    }

    private void setArgsToFragment(Fragment fragment, Bundle bundle) {
        fragment.setArguments(bundle);
    }

    public void switchBranch(final Route route, final Bundle args) {
        clearBackStack();
        replace(route, args);
    }

    private boolean validateState() {
        return activity != null && !activity.isFinishing();
    }

    public Fragment getCurrentFragment() {
        return activity.getSupportFragmentManager().findFragmentById(containerId);
    }

    public boolean empty() {
        return getCurrentFragment() == null;
    }

    public void pop() {
        try {
            FragmentManager fm = activity.getSupportFragmentManager();
            if (fm.getBackStackEntryCount() > 1) {
                fm.popBackStack();
            } else {
                activity.finish();
            }
        } catch (IllegalStateException e) {
            Timber.e(e, "Can't pop fragment"); //for avoid application crash when called at runtime
        }
    }

    public enum Action {
        ADD, REPLACE, POP
    }
}
