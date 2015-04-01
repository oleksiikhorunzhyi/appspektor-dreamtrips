package com.techery.spares.ui.fragment;

import android.app.Activity;
import android.os.Bundle;
import android.support.v4.app.DialogFragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.techery.spares.module.Annotations.Global;
import com.techery.spares.module.Injector;
import com.techery.spares.ui.activity.InjectingActivity;

import javax.inject.Inject;

import dagger.ObjectGraph;
import de.greenrobot.event.EventBus;

public abstract class InjectingDialogFragment extends DialogFragment implements ConfigurableFragment, Injector {
    private ObjectGraph objectGraph;

    @Inject
    @Global
    protected EventBus eventBus;

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
        this.objectGraph = getInitialObjectGraph();

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

    protected ObjectGraph getInitialObjectGraph() {
        return ((InjectingActivity) getActivity()).getObjectGraph();
    }


    @Override
    public void inject(Object target) {
        this.objectGraph.inject(target);
    }

    @Override
    public ObjectGraph getObjectGraph() {
        return this.objectGraph;
    }

}
