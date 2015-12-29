package com.messenger.synchmechanism;

import android.app.Activity;
import android.app.Application;
import android.content.Context;
import android.os.Bundle;
import android.os.Handler;

import java.util.ArrayList;
import java.util.List;


public class ActivityWatcher implements Application.ActivityLifecycleCallbacks {
    private static final int TIMER_FOR_DISCONNECT = 2000;

    Handler handler;

    List<OnStartStopAppListener> listeners;
    int visibleActivityCount;

    public ActivityWatcher(Context context) {
        this.handler = new Handler();
        this.visibleActivityCount = 0;
        this.listeners = new ArrayList<>();

        ((Application) context.getApplicationContext()).registerActivityLifecycleCallbacks(this);
    }

    public void addOnStartStopListener(OnStartStopAppListener listener) {
        listeners.add(listener);
    }

    public void removeOnStartStopListener(OnStartStopAppListener listener) {
        listeners.remove(listener);
    }

    @Override
    public void onActivityCreated(Activity activity, Bundle savedInstanceState) {

    }

    @Override
    public void onActivityStarted(Activity activity) {
        visibleActivityCount++;
        if (visibleActivityCount != 1) return;

        handler.post(() -> {
            for (OnStartStopAppListener listener : listeners) {
                listener.onStartApplication();
            }
        });
    }

    @Override
    public void onActivityResumed(Activity activity) {

    }

    @Override
    public void onActivityPaused(Activity activity) {

    }

    @Override
    public void onActivityStopped(Activity activity) {
        visibleActivityCount--;

        handler.postDelayed(() -> {
            if (visibleActivityCount != 0) return;

            for (OnStartStopAppListener listener : listeners) {
                listener.onStopApplication();
            }
        }, TIMER_FOR_DISCONNECT);
    }

    @Override
    public void onActivitySaveInstanceState(Activity activity, Bundle outState) {

    }

    @Override
    public void onActivityDestroyed(Activity activity) {
    }

    public interface OnStartStopAppListener {

        void onStartApplication();

        void onStopApplication();

    }
}
