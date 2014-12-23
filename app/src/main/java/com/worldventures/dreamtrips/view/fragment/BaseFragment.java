package com.worldventures.dreamtrips.view.fragment;


import android.app.Activity;
import android.support.v4.app.Fragment;

import com.worldventures.dreamtrips.view.activity.BaseActivity;
import com.worldventures.dreamtrips.view.activity.Injector;

public class BaseFragment<T extends BaseActivity> extends Fragment {

    private T activity;
    private Injector injector;

    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
        this.activity = (T) activity;
    }

    public void informUser(String st) {
        getAbsActivity().informUser(st);
    }

    public T getAbsActivity() {
        return activity;
    }

    public Injector getInjector() {
        return injector;
    }
}
