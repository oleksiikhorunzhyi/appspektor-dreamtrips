package com.worldventures.dreamtrips.core.navigation;

import android.os.Bundle;
import android.support.v4.app.DialogFragment;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;

import com.fourmob.datetimepicker.date.DatePickerDialog;
import com.techery.spares.module.qualifier.Global;
import com.worldventures.dreamtrips.BuildConfig;
import com.worldventures.dreamtrips.core.component.ComponentDescription;
import com.worldventures.dreamtrips.modules.common.view.activity.BaseActivity;
import com.worldventures.dreamtrips.modules.common.view.fragment.BaseFragment;

import java.util.Calendar;
import java.util.Date;

import javax.inject.Inject;

import de.greenrobot.event.EventBus;
import timber.log.Timber;

public class FragmentCompass {

    private BaseActivity activity;
    private OnTransactionListener onTransactionListener;

    private int containerId;
    private boolean backStackEnabled = true;
    private FragmentManager supportFragmentManager;

    public FragmentCompass(BaseActivity activity, int containerId) {
        this.activity = activity;
        this.containerId = containerId;
        supportFragmentManager = activity.getSupportFragmentManager();
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

    public void clear() {
        removeDetailed();
        removeEdit();
    }

    public void removeDetailed() {
        remove(Route.DETAIL_BUCKET.getClazzName());
    }

    public void removeEdit() {
        remove(Route.BUCKET_EDIT.getClazzName());
    }

    public void removePost() {
        remove(Route.POST_CREATE.getClazzName());
    }

    public void remove(String name) {
        if (validateState()) {
            FragmentManager fragmentManager = supportFragmentManager;
            Fragment fragment = fragmentManager.findFragmentByTag(name);

            if (fragment != null) {
                FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
                fragmentTransaction.remove(fragment);

                if (BuildConfig.DEBUG) {
                    fragmentTransaction.commit();
                } else {
                    fragmentTransaction.commitAllowingStateLoss();
                }

            }
        }
    }

    public void setSupportFragmentManager(FragmentManager supportFragmentManager) {
        this.supportFragmentManager = supportFragmentManager;
    }

    public FragmentManager getFragmentManager() {
        return supportFragmentManager;
    }

    protected void action(Action action, Route route, Bundle bundle) {
        if (validateState()) {
            try {
                String clazzName = route.getClazzName();

                BaseFragment fragment = (BaseFragment) Fragment.instantiate(activity, clazzName);
                setArgsToFragment(fragment, bundle);
                FragmentTransaction fragmentTransaction = supportFragmentManager.beginTransaction();

                switch (action) {
                    case REPLACE:
                        fragmentTransaction.replace(containerId, fragment, clazzName);
                        break;
                    case ADD:
                        fragmentTransaction.add(containerId, fragment, clazzName);
                        break;
                }

                if (onTransactionListener != null) {
                    onTransactionListener.onTransactionDone(null, action);
                }

                if (backStackEnabled) {
                    fragmentTransaction.addToBackStack(route.name());
                }

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

    public void showDatePickerDialog(DatePickerDialog.OnDateSetListener onDateSetListener,
                                     Date date) {
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(date);
        showDatePickerDialog(onDateSetListener, calendar, 0, 0, "default");
    }

    public void showDatePickerDialog(DatePickerDialog.OnDateSetListener onDateSetListener,
                                     Calendar calendar, int from, int to, String tag) {
        DatePickerDialog datePickerDialog = DatePickerDialog.newInstance
                (onDateSetListener, calendar.get(Calendar.YEAR), calendar.get(Calendar.MONTH), calendar.get(Calendar.DAY_OF_MONTH), false);

        if (from != 0 && to != 0) {
            datePickerDialog.setYearRange(from, to);
        }

        show(datePickerDialog, tag);
    }

    public void disableBackStack() {
        backStackEnabled = false;
    }

    private void setArgsToFragment(BaseFragment fragment, Bundle bundle) {
        fragment.setArguments(bundle);
    }

    public void switchBranch(final Route route, final Bundle args) {
        clearBackStack();
        replace(route, args);
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
            Timber.e(e, "Can't pop fragment"); //for avoid application crash when called at runtime
        }
        if (onTransactionListener != null) {
            onTransactionListener.onTransactionDone(null, Action.POP);
        }
    }

    public enum Action {
        ADD, REPLACE, POP
    }

    public interface OnTransactionListener {
        void onTransactionDone(Route route, Action action);
    }
}
