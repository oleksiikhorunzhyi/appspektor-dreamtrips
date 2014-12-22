package com.worldventures.dreamtrips.core.navigation;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;

import com.worldventures.dreamtrips.BuildConfig;
import com.worldventures.dreamtrips.R;
import com.worldventures.dreamtrips.utils.Logs;
import com.worldventures.dreamtrips.view.activity.BaseActivity;
import com.worldventures.dreamtrips.view.fragment.BaseFragment;

public class FragmentCompass {

    protected String TAG = getClass().getSimpleName();
    BaseActivity activity;
    OnTransactionListener onTransactionListener;
    private int containerId;

    public FragmentCompass(BaseActivity activity) {
        this.activity = activity;
        setContainerId(R.id.container);//TODO temp
    }

    public void setContainerId(int containerId) {
        this.containerId = containerId;
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
                Logs.e(TAG, "TransitionManager error", e);
            }
        } else {
            Logs.e(TAG, new IllegalStateException("Incorrect call of transaction manager action. validateState() false."));
        }
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
        Logs.d(TAG, "switch branch");
    }

    private boolean validateState() {
        return activity != null && !activity.isFinishing();
    }

    public BaseFragment getCurrentFragment() {
        return (BaseFragment) activity.getSupportFragmentManager().findFragmentById(containerId);
    }

    protected void clearBackStack() {
        try {
            FragmentManager fragmentManager = activity.getSupportFragmentManager();
            fragmentManager.popBackStackImmediate(0, FragmentManager.POP_BACK_STACK_INCLUSIVE);
        } catch (IllegalStateException e) {
            Logs.e(TAG, "TransitionManager error", e); //for avoid application crash when called at runtime
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
            Logs.e(BaseActivity.class.getSimpleName(), e); //for avoid application crash when called at runtime
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
