package com.worldventures.dreamtrips.view.fragment;


import android.app.Activity;
import android.support.v4.app.Fragment;

import com.techery.spares.ui.fragment.InjectingFragment;
import com.worldventures.dreamtrips.view.activity.BaseActivity;
import com.worldventures.dreamtrips.view.presentation.IInformView;

public class BaseFragment<T extends BaseActivity> extends InjectingFragment implements IInformView {

    private T activity;

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
}
