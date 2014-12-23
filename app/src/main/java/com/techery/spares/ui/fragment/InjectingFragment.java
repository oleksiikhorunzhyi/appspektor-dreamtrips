package com.techery.spares.ui.fragment;

import android.app.Activity;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.techery.spares.module.Annotations.Global;
import com.techery.spares.module.Injector;

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
        this.eventBus.registerSticky(this);
    }

    @Override
    public void onPause() {
        super.onPause();
        this.eventBus.unregister(this);
    }

    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
        FragmentHelper.inject(activity, this);
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
