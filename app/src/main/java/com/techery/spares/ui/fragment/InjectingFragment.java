package com.techery.spares.ui.fragment;

import android.app.Activity;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.View;
import android.view.ViewGroup;

import com.techery.spares.annotations.MenuResource;
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
    public EventBus eventBus;

    public interface Events {
        class ReloadEvent {
        }
    }

    public void onEvent(Events.ReloadEvent reloadEvent) {
        //do nothing
    }

    public EventBus getEventBus() {
        return eventBus;
    }

    public void afterCreateView(View rootView) {
        setupMenuIfNeed();
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
        } catch (Exception e) {
            Log.e(InjectingFragment.class.getSimpleName(), "", e);
        }
    }

    private void setupMenuIfNeed() {
        MenuResource menuResource = this.getClass().getAnnotation(MenuResource.class);
        setHasOptionsMenu(menuResource != null);
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

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        MenuResource menuResource = this.getClass().getAnnotation(MenuResource.class);
        if (menuResource != null) {
            inflater.inflate(menuResource.value(), menu);
        }
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
