package com.worldventures.dreamtrips.core.navigation;

import android.os.Bundle;
import android.support.v4.app.DialogFragment;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;

import com.worldventures.dreamtrips.BuildConfig;
import com.worldventures.dreamtrips.modules.common.view.activity.BaseActivity;
import com.worldventures.dreamtrips.modules.common.view.dialog.BaseDialogFragment;
import com.worldventures.dreamtrips.modules.common.view.fragment.BaseFragment;

import timber.log.Timber;

public class FragmentCompass {

    BaseActivity activity;
    OnTransactionListener onTransactionListener;

    private int containerId;

    public FragmentCompass(BaseActivity activity, int containerId) {
        this.activity = activity;
        setContainerId(containerId);
    }

    public void setContainerId(int containerId) {
        this.containerId = containerId;
    }

    public void show(DialogState state) {
        show(state, null);
    }

    public void show(DialogState state, Bundle bundle) {
        showDialog(state, bundle);
    }

    public void add(State state) {
        add(state, null);
    }

    public void add(State state, Bundle bundle) {
        action(Action.ADD, state, bundle);
    }

    public void replace(State state) {
        replace(state, null);
    }

    public void replace(State state, Bundle bundle) {
        action(Action.REPLACE, state, bundle);
    }

    protected void action(Action action, State state, Bundle bundle) {
        if (validateState()) {
            try {
                FragmentManager supportFragmentManager = activity.getSupportFragmentManager();
                String clazzName = state.getClazzName();

                BaseFragment fragment = (BaseFragment) Fragment.instantiate(activity, clazzName);
                setArgsToFragment(fragment, bundle);
                FragmentTransaction fragmentTransaction = supportFragmentManager.beginTransaction();
                switch (action) {
                    case REPLACE:
                        fragmentTransaction.replace(containerId, fragment);
                        if (onTransactionListener != null) {
                            onTransactionListener.onTransactionDone(state, Action.REPLACE);
                        }
                        break;
                    case ADD:
                        fragmentTransaction.add(containerId, fragment);
                        if (onTransactionListener != null) {
                            onTransactionListener.onTransactionDone(null, Action.ADD);
                        }
                        break;
                }
                fragmentTransaction.addToBackStack(clazzName);
                if (BuildConfig.DEBUG) {
                    fragmentTransaction.commit();
                } else {
                    fragmentTransaction.commitAllowingStateLoss();
                }
            } catch (Exception e) {
                Timber.e("TransitionManager error", e);
            }
        } else {
            Timber.e(new IllegalStateException("Incorrect call of transaction manager action. validateState() false."), "");
        }
    }

    public void show(DialogFragment dialogFragment, String tag) {
        if (validateState()) {
            FragmentManager supportFragmentManager = activity.getSupportFragmentManager();
            dialogFragment.show(supportFragmentManager, tag);
        }
    }

    protected void showDialog(DialogState state, Bundle bundle) {
        if (validateState()) {
            FragmentManager supportFragmentManager = activity.getSupportFragmentManager();
            String clazzName = state.getClazzName();
            BaseDialogFragment dialogFragment = (BaseDialogFragment) DialogFragment.instantiate(activity, clazzName);
            dialogFragment.setArguments(bundle);

            FragmentTransaction ft = supportFragmentManager.beginTransaction();
            Fragment prev = supportFragmentManager.findFragmentByTag("dialog");
            if (prev != null) {
                ft.remove(prev);
            }
            ft.addToBackStack(null);

            dialogFragment.show(supportFragmentManager, "dialog");
        } else {
            Timber.e(new IllegalStateException("Incorrect call of transaction manager action. validateState() false."), "");
        }
    }

    public int getPreviousFragmentTitle() {
        return getPreviousFragment().getTitle();
    }

    public State getPreviousFragment() {
        FragmentManager fm = activity.getSupportFragmentManager();
        if (fm.getBackStackEntryCount() > 2) {
            FragmentManager.BackStackEntry backEntry =
                    fm.getBackStackEntryAt(fm.getBackStackEntryCount() - 2);
            String str = backEntry.getName();
            return State.restoreByClass(str);
        }
        return State.DREAMTRIPS;

    }

    private void setArgsToFragment(BaseFragment fragment, Bundle bundle) {
        fragment.setArguments(bundle);
    }

    public void switchBranch(final State state) {
        switchBranch(state, null);
    }

    public void switchBranch(final State state, final Bundle args) {
        clearBackStack();
        replace(state, args);
    }

    private boolean validateState() {
        return activity != null && !activity.isFinishing();
    }

    public BaseFragment getCurrentFragment() {
        return (BaseFragment) activity.getSupportFragmentManager().findFragmentById(containerId);
    }

    public State getCurrentState() {
        return State.restoreByClass(getCurrentFragment().getClass().getName());
    }

    protected void clearBackStack() {
        try {
            FragmentManager fragmentManager = activity.getSupportFragmentManager();
            fragmentManager.popBackStackImmediate(0, FragmentManager.POP_BACK_STACK_INCLUSIVE);
        } catch (IllegalStateException e) {
            Timber.e("TransitionManager error", e); //for avoid application crash when called at runtime
        }
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
            Timber.e(BaseActivity.class.getSimpleName(), e); //for avoid application crash when called at runtime
        }
        if (onTransactionListener != null) {
            onTransactionListener.onTransactionDone(null, Action.POP);
        }
    }

    public void setOnTransactionListener(OnTransactionListener onTransactionListener) {
        this.onTransactionListener = onTransactionListener;
    }

    public enum Action {
        ADD, REPLACE, POP
    }

    public static interface OnTransactionListener {
        public void onTransactionDone(State state, Action action);
    }
}
