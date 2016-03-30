package com.worldventures.dreamtrips.core.flow.util;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.FragmentActivity;
import android.view.View;

import java.lang.ref.WeakReference;

import flow.Flow;
import flow.FlowDelegate;
import flow.History;
import flow.StateParceler;

import static flow.FlowDelegate.NonConfigurationInstance;

public class FlowActivityHelper {

    private WeakReference<Activity> activityRef;
    //
    protected FlowDelegate flowSupport;
    protected StateParceler parceler;
    protected Flow.Dispatcher dispatcher;
    protected History defaultBackstack;

    private boolean created;

    public FlowActivityHelper(Activity activity, Flow.Dispatcher dispatcher, History defaultBackstack, StateParceler parceler) {
        this.activityRef = new WeakReference<>(activity);
        this.dispatcher = dispatcher;
        this.defaultBackstack = defaultBackstack;
        this.parceler = parceler;
    }

    public void onCreate(Bundle savedInstanceState) {
        created = true;
        Activity activity = activityRef.get();
        if (activity == null) throw new IllegalStateException("Can't create scope for null activity");

        NonConfigurationInstance nonConfig;
        if (activity instanceof FragmentActivity) {
            nonConfig = (NonConfigurationInstance) ((FragmentActivity) activity).getLastCustomNonConfigurationInstance();
        } else {
            nonConfig = (NonConfigurationInstance) activity.getLastNonConfigurationInstance();
        }
        flowSupport = FlowDelegate.onCreate(nonConfig, activity.getIntent(), savedInstanceState, parceler, defaultBackstack, dispatcher);

        if (Flow.get(activity).getHistory() != null) {
            defaultBackstack = null;
        }
    }

    public boolean isCreated() {
        return created;
    }

    public Object provideNonConfigurationInstance() {
        return flowSupport.onRetainNonConfigurationInstance();
    }

    public void onNewIntent(Intent intent) {
        flowSupport.onNewIntent(intent);
    }

    public void onSaveState(Bundle outState, View container) {
        flowSupport.onSaveInstanceState(outState, container);
    }

    public void onResume() {
        if (flowSupport != null) flowSupport.onResume();
    }

    public void onPause() {
        if (flowSupport != null) {
            flowSupport.onPause();
        }
    }

    public boolean handleBack() {
        return flowSupport.onBackPressed();
    }

    @Nullable
    public Object getSystemService(String name) {
        Object flow = null;
        if (flowSupport != null) flow = flowSupport.getSystemService(name);
        return flow;
    }
}
