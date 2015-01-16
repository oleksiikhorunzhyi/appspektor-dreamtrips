package com.techery.spares.ui.fragment;

import android.app.Activity;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.techery.spares.module.Annotations.Global;
import com.techery.spares.module.Injector;
import com.techery.spares.ui.activity.InjectingActivity;

import javax.inject.Inject;

import dagger.ObjectGraph;
import de.greenrobot.event.EventBus;

public abstract class InjectingFragment extends Fragment implements ConfigurableFragment, Injector {
    private ObjectGraph objectGraph;

    @Inject
    @Global
    EventBus eventBus;

    public interface Events {
        class ReloadEvent {}
    }

    public void onEvent(Events.ReloadEvent reloadEvent) {

    }

    public EventBus getEventBus() {
        return eventBus;
    }

    public void afterCreateView(View rootView) {

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        return FragmentHelper.onCreateView(inflater, container, this);
    }

    @Override
    public void onResume() {
        super.onResume();
        try {
            this.eventBus.registerSticky(this);
        } catch (Throwable ignored) {

        }
    }

    @Override
    public void onPause() {
        super.onPause();
        this.eventBus.unregister(this);
    }

    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);

        this.objectGraph = getInitialObjectGraph();

        FragmentHelper.inject(activity, this);
    }

    protected ObjectGraph getInitialObjectGraph() {
        return ((InjectingActivity)getActivity()).getObjectGraph();
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
