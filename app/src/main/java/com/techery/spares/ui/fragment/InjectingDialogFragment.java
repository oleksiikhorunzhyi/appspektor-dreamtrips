package com.techery.spares.ui.fragment;

import android.app.Activity;
import android.os.Bundle;
import android.support.v4.app.DialogFragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.techery.spares.module.Annotations.Global;

import javax.inject.Inject;

import de.greenrobot.event.EventBus;

public abstract class InjectingDialogFragment extends DialogFragment implements ConfigurableFragment {
    @Inject
    @Global
    EventBus eventBus;

    public void onEvent(InjectingFragment.Events.ReloadEvent reloadEvent) {

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        return FragmentHelper.onCreateView(inflater, container, this);
    }

    public void afterCreateView(View rootView) {

    }

    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
        FragmentHelper.inject(activity, this);
    }

    @Override
    public void onResume() {
        super.onResume();
        this.eventBus.registerSticky(this);
    }

    @Override
    public void onPause() {
        super.onPause();
        this.eventBus.unregister(this);
    }
}
